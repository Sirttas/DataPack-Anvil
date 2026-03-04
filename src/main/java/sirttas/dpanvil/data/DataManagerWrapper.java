package sirttas.dpanvil.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.data.serializer.CodecJsonDataSerializer;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;
import sirttas.dpanvil.registry.RegistryListener;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class DataManagerWrapper extends ContextAwareReloadListener implements PreparableReloadListener {

	private final Map<ResourceKey<@NotNull IDataManager<?>>, IDataManager<?>> managers = Maps.newHashMap();
	private final Map<ResourceKey<@NotNull IDataManager<?>>, IJsonDataSerializer<?, ?>> serializers = Maps.newHashMap();

	public static <T> void logManagerException(ResourceKey<? super IDataManager<T>> key, Throwable e) {
		if (e != null) {
			DataPackAnvilApi.LOGGER.error(() -> "Exception while loading data for manager " + key + ":", e);
		}
	}

	public <T, M extends IDataManager<T>> M getManager(ResourceKey<? super IDataManager<T>> key) {
		return (M) managers.get(key);
	}

	public <T, M extends IDataManager<T>> M getManager(Class<T> clazz) {
		return (M) managers.values().stream()
				.filter(manager -> manager.getContentType().isAssignableFrom(clazz))
				.findAny()
				.orElse(null);
	}

	public <T> ResourceKey<@NotNull IDataManager<T>> getKey(IDataManager<T> manager) {
		return managers.entrySet().stream()
				.filter(e -> e.getValue().equals(manager))
				.map(e -> this.<ResourceKey<@NotNull IDataManager<T>>>cast(e.getKey()))
				.findAny()
				.orElseGet(() -> IDataManager.createManagerKey(DPAnvilNames.Identifiers.NONE));
	}

	private <T> T cast(Object key) {
		return (T)key;
	}

	public <T, I, S extends IJsonDataSerializer<T, I>> S getSerializer(ResourceKey<? super IDataManager<T>> key) {
		return (S) serializers.get(key);
	}

	public <T> void putManagerFromIMC(Supplier<?> supplier) {
		DataManagerIMC<T> message = (DataManagerIMC<T>) supplier.get();
		IDataManager<T> manager = message.getManager();
		ResourceKey<@NotNull IDataManager<?>> key = this.cast(message.getKey());

		serializers.put(key, buildSerializer(message));
		managers.put(key, manager);
	}
	
	private <T> IJsonDataSerializer<T, ?> buildSerializer(DataManagerIMC<T> message) {
		var codec = message.getCodec();
		var readJson = message.getReadJson();

		return codec != null ? new CodecJsonDataSerializer<>(codec) : new IJsonDataSerializer<T, T>() {

			@Override
			public T read(JsonElement json) {
				if (readJson != null) {
					return readJson.apply(json);
				}
				throw new IllegalStateException("trying to read json without the proper serialization tools for manager : " + message.getKey() + " makes sure you provide a correct json serializer to it.");
			}

			@Override
			public T read(FriendlyByteBuf buf) {
				return message.getReadPacket().apply(buf);
			}

			@Override
			public T read(T data) {
				return data;
			}

			@Override
			public void write(T data, FriendlyByteBuf buf) {
				message.getWritePacket().accept(buf, data);
			}
		};
	}


	public Collection<ResourceKey<@NotNull IDataManager<?>>> ids() {
		return managers.keySet();
	}

	public Map<ResourceKey<@NotNull IDataManager<?>>, IDataManager<?>> getDataManagers() {
		return managers;
	}

	@Override
	public void injectContext(@NotNull ICondition.IContext context, @NotNull HolderLookup.Provider registryLookup) {
		managers.values().forEach(m -> {
			if (m instanceof ContextAwareReloadListener listener) {
				listener.injectContext(context, registryLookup);
			}
		});
	}

	@Override
    public @NotNull CompletableFuture<Void> reload(@NotNull SharedState sharedState, @NotNull Executor backgroundExecutor, @NotNull PreparationBarrier stage, @NotNull Executor gameExecutor) {
		if (managers.isEmpty()) {
			return CompletableFuture.allOf();
		}
		return CompletableFuture.runAsync(() -> CompletableFuture.allOf(managers.entrySet().stream()
						.map(entry -> entry.getValue().reload(sharedState, backgroundExecutor, stage, gameExecutor)
								.handle(handleManagerException(entry.getKey())))
						.toArray(CompletableFuture[]::new)), backgroundExecutor)
				.thenCompose(stage::wait)
				.thenRunAsync(() -> RegistryListener.getInstance().listen(r -> postLoad()), gameExecutor);
	}

	private void postLoad() {
		DataPackAnvilApi.LOGGER.debug("DataManagers loading complete: {}", () -> {
			StringBuilder logBuilder = new StringBuilder();

			this.managers.forEach((managerId, manager) -> {
				logBuilder.append("\r\n")
						.append(managerId)
						.append(" ")
						.append(manager.getData().size())
						.append(" entries:\r\n");
				manager.getData().forEach((id, data) -> logBuilder
						.append("\t")
						.append(id)
						.append(": ")
						.append(data)
						.append("\r\n"));
			});
			return logBuilder.toString();
		});
	}
	
	private BiFunction<Void, Throwable, Void> handleManagerException(ResourceKey<@NotNull IDataManager<?>> key) {
		return (r, e) -> {
			logManagerException(key, e);
			return r;
		};
	}
}
