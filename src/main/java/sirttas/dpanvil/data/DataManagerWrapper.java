package sirttas.dpanvil.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.data.manager.AbstractDataManager;
import sirttas.dpanvil.data.serializer.CodecJsonDataSerializer;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class DataManagerWrapper implements PreparableReloadListener {

	private final Map<ResourceKey<IDataManager<?>>, IDataManager<?>> managers = Maps.newHashMap();
	private final Map<ResourceKey<IDataManager<?>>, IJsonDataSerializer<?>> serializers = Maps.newHashMap();
	private final Map<DynamicOps<?>, RegistryOps<?>> registryOps = new Reference2ObjectOpenHashMap<>();

	private RegistryAccess registry = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

	public static <T> void logManagerException(ResourceKey<? super IDataManager<T>> key, Throwable e) {
		if (e != null) {
			DataPackAnvilApi.LOGGER.error(() -> "Exception while loading data for manager " + key + ":", e);
		}
	}

	public void setRegistry(RegistryAccess registry) {
		this.registry = registry;
		this.registryOps.clear();
	}

	public synchronized <T> RegistryOps<T> getRegistryOps(DynamicOps<T> ops) {
		return (RegistryOps<T>) registryOps.computeIfAbsent(ops, o -> RegistryOps.create(o, registry));
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

	public <T> ResourceKey<IDataManager<T>> getKey(IDataManager<T> manager) {
		return managers.entrySet().stream()
				.filter(e -> e.getValue().equals(manager))
				.map(e -> this.<ResourceKey<IDataManager<T>>>cast(e.getKey()))
				.findAny()
				.orElseGet(() -> IDataManager.createManagerKey(DataPackAnvilApi.ID_NONE));
	}

	private <T> T cast(Object key) {
		return (T)key;
	}

	public <T, S extends IJsonDataSerializer<T>> S getSerializer(ResourceKey<? super IDataManager<T>> key) {
		return (S) serializers.get(key);
	}

	public <T> void putManagerFromIMC(Supplier<?> supplier) {
		DataManagerIMC<T> message = (DataManagerIMC<T>) supplier.get();
		var key = message.getKey();
		ResourceKey<IDataManager<?>> castedKey = this.cast(key);
		IDataManager<T> manager = message.getManager();

		serializers.put(castedKey, buildSerializer(message));
		managers.put(castedKey, manager);
		if (manager instanceof AbstractDataManager) {
			((AbstractDataManager<T, ?>) manager).setKey(key);
		}
	}
	
	private <T> IJsonDataSerializer<T> buildSerializer(DataManagerIMC<T> message) {
		Codec<T> codec = message.getCodec();
		return codec != null ? new CodecJsonDataSerializer<>(codec) : new IJsonDataSerializer<>() {

			@Override
			public T read(JsonElement json) {
				Function<JsonElement, T> readJson = message.getReadJson();

				if (readJson != null) {
					return readJson.apply(json);
				}
				throw new IllegalStateException("trying to read json without the proper serialization tools for manager : "
						+ message.getKey() + " makes sure you provide a correct json serializer to it.");
			}

			@Override
			public T read(FriendlyByteBuf buf) {
				return message.getReadPacket().apply(buf);
			}

			@Override
			public void write(T data, FriendlyByteBuf buf) {
				message.getWritePacket().accept(buf, data);
			}
		};
	}


	public Collection<ResourceKey<IDataManager<?>>> ids() {
		return managers.keySet();
	}

	public Map<ResourceKey<IDataManager<?>>, IDataManager<?>> getDataManagers() {
		return managers;
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
		if (!ModLoader.isLoadingStateValid() || managers.isEmpty()) {
			return CompletableFuture.allOf();
		}
		return CompletableFuture.runAsync(registryOps::clear, backgroundExecutor)
				.thenComposeAsync(v -> CompletableFuture.allOf(managers.entrySet().stream()
						.map(entry -> entry.getValue().reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)
								.handle(handleManagerException(entry.getKey())))
						.toArray(CompletableFuture[]::new)), backgroundExecutor)
				.thenCompose(stage::wait)
				.thenRunAsync(() -> TagListener.listen(this::postLoad), gameExecutor);
	}

	private void postLoad() {
		DataPackAnvilApi.LOGGER.debug("DataManagers loading compleat: {}", () -> {
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
	
	private BiFunction<Void, Throwable, Void> handleManagerException(ResourceKey<IDataManager<?>> key) {
		return (r, e) -> {
			logManagerException(key, e);
			return r;
		};
	}
}
