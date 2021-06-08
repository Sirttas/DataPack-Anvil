package sirttas.dpanvil.data;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.data.manager.AbstractDataManager;
import sirttas.dpanvil.data.serializer.CodecJsonDataSerializer;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;
import sirttas.dpanvil.tag.DataTagManager;

public class DataManagerWrapper implements IFutureReloadListener {

	private final Map<ResourceLocation, IDataManager<?>> managers = Maps.newHashMap();
	private final Map<ResourceLocation, IJsonDataSerializer<?>> serializers = Maps.newHashMap();

	public static void logManagerException(ResourceLocation id, Throwable e) {
		if (e != null) {
			DataPackAnvilApi.LOGGER.error(() -> "Exception while loading data for manager " + id.toString() + ":", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T, M extends IDataManager<T>> M getManager(ResourceLocation id) {
		return (M) managers.get(id);
	}

	@SuppressWarnings("unchecked")
	public <T, M extends IDataManager<T>> M getManager(Class<T> clazz) {
		return (M) managers.values().stream().filter(manager -> manager.getContentType().isAssignableFrom(clazz)).findAny().orElse(null);
	}

	public <T> ResourceLocation getId(IDataManager<T> manager) {
		return managers.entrySet().stream().filter(e -> e.getValue().equals(manager)).map(Entry::getKey).findAny().orElse(DataPackAnvilApi.ID_NONE);
	}
	
	@SuppressWarnings("unchecked")
	public <T, S extends IJsonDataSerializer<T>> S getSerializer(ResourceLocation id) {
		return (S) serializers.get(id);
	}

	public <T> void putManagerFromIMC(Supplier<DataManagerIMC<T>> imc) {
		DataManagerIMC<T> message = imc.get();
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
		return codec != null ? new CodecJsonDataSerializer<>(codec) : new IJsonDataSerializer<T>() {

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
			public T read(PacketBuffer buf) {
				return message.getReadPacket().apply(buf);
			}

			@Override
			public void write(T data, PacketBuffer buf) {
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
	public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		if (ModLoader.isLoadingStateValid() && !managers.isEmpty()) {
			CompletableFuture<Void> completableFuture = CompletableFuture.allOf(managers.entrySet().stream()
							.map(entry -> {
									IDataManager<?> manager = entry.getValue();
									return manager.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)
											.thenRun(() -> MinecraftForge.EVENT_BUS.post(new DataManagerReloadEvent<>(manager)))
											.handle(handleManagerException(entry.getKey()));
								}).toArray(CompletableFuture[]::new));

			if (DataPackAnvil.DATA_TAG_MANAGER.shouldLoad()) {
				completableFuture = completableFuture
						.thenCompose(v -> DataPackAnvil.DATA_TAG_MANAGER.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)
								.handle(handleManagerException(DataTagManager.ID)));
			}
			return completableFuture.thenRun(this::postLoad);
		}
		return CompletableFuture.allOf();
	}

	private void postLoad() {
		DataPackAnvil.ANNOTATION_PROCESSOR.applyDataHolder();
		DataPackAnvilApi.LOGGER.debug("DataManagers loading compleat: {}", () -> {
			StringBuilder logBuilder = new StringBuilder();

			this.managers.forEach((managerId, manager) -> {
				logBuilder.append("\r\n" + managerId + " " + manager.getData().size() + " entries:\r\n");
				manager.getData().forEach((id, data) -> logBuilder.append("\t" + id + ": " + data + "\r\n"));
			});
			logBuilder.append("\r\nData tags:");
			DataPackAnvil.DATA_TAG_MANAGER.getData().forEach((collectionId, tagCollection) -> logTags(logBuilder, collectionId, tagCollection));
			return logBuilder.toString();
		});
	}

	private <T> void logTags(StringBuilder logBuilder, ResourceLocation collectionId, ITagCollection<T> tagCollection) {
		Map<ResourceLocation, ITag<T>> map = tagCollection.getAllTags();
		
		logBuilder.append("\r\n" + collectionId + " " + map.size() + " tags:\r\n");
		map.forEach((id, tag) -> logBuilder.append("\t" + id + ": " + tag.getValues().size() + " elements\r\n"));
	}
	
	private BiFunction<Void, Throwable, Void> handleManagerException(ResourceLocation id) {
		return (r, e) -> {
			logManagerException(id, e);
			return r;
		};
	}
}
