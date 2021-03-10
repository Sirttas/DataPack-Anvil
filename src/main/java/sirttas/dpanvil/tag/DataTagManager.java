package sirttas.dpanvil.tag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.ICodecProvider;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.imc.DataTagIMC;
import sirttas.dpanvil.api.tag.DataTagRegistry;

public class DataTagManager implements IDataManager<ITagCollection<?>>, ICodecProvider<Map<ResourceLocation, ITagCollection<?>>> {
	
	private static final Codec<Pair<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>>> MAP_CODEC = Codec.pair(
			ResourceLocation.CODEC.fieldOf(DPAnvilNames.ID).codec(),
			Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC.listOf()).fieldOf(DPAnvilNames.VALUES).codec());
	private static final String FOLDER = DataPackAnvilApi.TAGS_FOLDER;
	
	private BiMap<ResourceLocation, ITagCollection<?>> tagCollections = ImmutableBiMap.of();
	private Map<ResourceLocation, DataTagRegistry<?>> tagRegistries = Maps.newHashMap();
	
	private final Codec<Map<ResourceLocation, ITagCollection<?>>> codec = Codec.unboundedMap(ResourceLocation.CODEC, new Codec<ITagCollection<?>>() {
		@Override
		public <T> DataResult<T> encode(ITagCollection<?> input, DynamicOps<T> ops, T prefix) {
			return MAP_CODEC.encode(mapCollection(input), ops, prefix);
		}

		private <U> Pair<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>> mapCollection(ITagCollection<U> input) {
			ResourceLocation id = getId(input);
			IDataManager<U> manager = DataPackAnvil.WRAPPER.getManager(id);
			Map<ResourceLocation, List<ResourceLocation>> map = Maps.newHashMap();

			input.getIDTagMap().forEach((key, tag) -> map.put(key, tag.getAllElements().stream()
					.map(manager::getId)
					.filter(location -> !DataPackAnvilApi.ID_NONE.equals(location))
					.collect(Collectors.toList())));
			return Pair.of(id, map);
		}

		@Override
		public <T> DataResult<Pair<ITagCollection<?>, T>> decode(DynamicOps<T> ops, T input) {
			return MAP_CODEC.decode(ops, input).map(pair -> pair.mapFirst(this::mapToCollection));
		}
		
		private <U> ITagCollection<U> mapToCollection(Pair<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>> input) {
			ResourceLocation id = input.getFirst();
			TagCollectionReader<U> reader = getTagCollectionReader(id, DataPackAnvil.WRAPPER.getManager(id));
			Map<ResourceLocation, ITag.Builder> map = Maps.newHashMap();
			
			
			input.getSecond().forEach((key, tag) -> {
				ITag.Builder builder = ITag.Builder.create();
				
				tag.forEach(location -> builder.addItemEntry(location, key.toString()));
				map.put(key, builder);
			});
			return reader.buildTagCollectionFromMap(map);
		}

	});
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<ITagCollection<?>> getContentType() {
		return (Class<ITagCollection<?>>) (Class<?>) ITagCollection.class; // double cast or we get a compile error
	}

	@Override
	public Map<ResourceLocation, ITagCollection<?>> getData() {
		return tagCollections;
	}

	public boolean shouldLoad() {
		return !tagRegistries.isEmpty();
	}
	
	@Override
	public void setData(Map<ResourceLocation, ITagCollection<?>> map) {
		tagCollections = ImmutableBiMap.copyOf(map);
		map.forEach(this::injectTagCollection);
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", tagCollections.size(), "DataPack Anvil TagCollection");
	}
	
	@Override
	public ResourceLocation getId(final ITagCollection<?> value) {
		return tagCollections.inverse().getOrDefault(value, DataPackAnvilApi.ID_NONE);
	}
	
	@Override
	public String getFolder() {
		return FOLDER;
	}

	public <T> void putTagRegistryFromIMC(Supplier<DataTagIMC<T>> messageSupplier) {
		DataTagIMC<T> imc = messageSupplier.get();
		
		tagRegistries.put(DataPackAnvil.WRAPPER.getId(imc.getManager()), imc.getRegistry());
	}
	
	@Override
	public Codec<Map<ResourceLocation, ITagCollection<?>>> getCodec() {
		return codec;
	}
	
	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		List<ResourceLocation> ids = Lists.newArrayList();
		List<TagCollectionReader<?>> readers = Lists.newArrayList();
		List<CompletableFuture<Map<ResourceLocation, ITag.Builder>>> completableFutures = Lists.newArrayList();
		
		DataPackAnvil.WRAPPER.getDataManagers().entrySet().stream().filter(e -> tagRegistries.get(e.getKey()) != null).forEach(entry -> {
			ResourceLocation id = entry.getKey();
			IDataManager<?> manager = entry.getValue();
			TagCollectionReader<?> reader = getTagCollectionReader(id, manager);
			
			ids.add(id);
			readers.add(reader);
			completableFutures.add(reader.readTagsFromManager(resourceManager, backgroundExecutor));
		});
		
		return CompletableFuture.allOf(completableFutures.stream().toArray(CompletableFuture[]::new)).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(v -> {
			Map<ResourceLocation, ITagCollection<?>> map = Maps.newHashMap();
			
			for (int i = 0; i < ids.size(); i++) {
				ResourceLocation id = ids.get(i);
				ITagCollection<?> collection = readers.get(i).buildTagCollectionFromMap(completableFutures.get(i).join());
				
				map.put(id, collection);
			}
			this.setData(map);
		}, gameExecutor);
	}

	@SuppressWarnings("unchecked")
	private <T> void injectTagCollection(ResourceLocation id, ITagCollection<T> collection) {
		DataTagRegistry<T> tagRegistry = (DataTagRegistry<T>) tagRegistries.get(id);
		
		tagRegistry.setCollection(collection);
	}
	
	private <T> TagCollectionReader<T> getTagCollectionReader(ResourceLocation id, IDataManager<T> manager) {
		return new TagCollectionReader<>(manager::getOptional, FOLDER + manager.getFolder(), id.toString());
	}
}
