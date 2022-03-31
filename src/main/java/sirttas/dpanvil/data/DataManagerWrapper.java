package sirttas.dpanvil.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class DataManagerWrapper implements PreparableReloadListener {

	private final Map<ResourceLocation, IDataManager<?>> managers = Maps.newHashMap();
	private final Map<ResourceLocation, IJsonDataSerializer<?>> serializers = Maps.newHashMap();

	public static void logManagerException(ResourceLocation id, Throwable e) {
		if (e != null) {
			DataPackAnvilApi.LOGGER.error(() -> "Exception while loading data for manager " + id.toString() + ":", e);
		}
	}
	
	public <T, M extends IDataManager<T>> M getManager(ResourceLocation id) {
		return (M) managers.get(id);
	}

	public <T, M extends IDataManager<T>> M getManager(Class<T> clazz) {
		return (M) managers.values().stream().filter(manager -> manager.getContentType().isAssignableFrom(clazz)).findAny().orElse(null);
	}

	public <T> ResourceLocation getId(IDataManager<T> manager) {
		return managers.entrySet().stream().filter(e -> e.getValue().equals(manager)).map(Entry::getKey).findAny().orElse(DataPackAnvilApi.ID_NONE);
	}
	
	public <T, S extends IJsonDataSerializer<T>> S getSerializer(ResourceLocation id) {
		return (S) serializers.get(id);
	}

	public <T> void putManagerFromIMC(Supplier<?> supplier) {
		DataManagerIMC<T> message = (DataManagerIMC<T>) supplier.get();
		ResourceLocation id = message.getId();
		IDataManager<T> manager = message.getManager();

		serializers.put(id, buildSerializer(message));
		managers.put(id, manager);
		if (manager instanceof AbstractDataManager) {
			((AbstractDataManager<?, ?>) manager).setId(id);
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
						+ message.getId() + " makes sure you provide a correct json serializer to it.");
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


	public Collection<ResourceLocation> ids() {
		return managers.keySet();
	}

	public Map<ResourceLocation, IDataManager<?>> getDataManagers() {
		return managers;
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor,
												   @NotNull Executor gameExecutor) {
		if (ModLoader.isLoadingStateValid() && !managers.isEmpty()) {
			CompletableFuture<Void> completableFuture = CompletableFuture.allOf(managers.entrySet().stream()
							.map(entry -> {
									IDataManager<?> manager = entry.getValue();
									return manager.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)
											.handle(handleManagerException(entry.getKey()));
								}).toArray(CompletableFuture[]::new));
			return completableFuture.thenRun(this::postLoad);
		}
		return CompletableFuture.allOf();
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
	
	private BiFunction<Void, Throwable, Void> handleManagerException(ResourceLocation id) {
		return (r, e) -> {
			logManagerException(id, e);
			return r;
		};
	}
}
