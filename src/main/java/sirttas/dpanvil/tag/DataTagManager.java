package sirttas.dpanvil.tag;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.ICodecProvider;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataWrapper;
import sirttas.dpanvil.api.imc.DataTagIMC;
import sirttas.dpanvil.api.tag.DataTagRegistry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class DataTagManager implements IDataManager<TagCollection<?>>, ICodecProvider<Map<ResourceLocation, TagCollection<?>>> {
	
	public static final ResourceLocation ID = DataPackAnvil.createRL("tags");
	private static final Codec<Pair<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>>> MAP_CODEC = Codec.pair(
			ResourceLocation.CODEC.fieldOf(DPAnvilNames.ID).codec(),
			Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC.listOf()).fieldOf(DPAnvilNames.VALUES).codec());
	private static final String FOLDER = DataPackAnvilApi.TAGS_FOLDER;
	
	private BiMap<ResourceLocation, TagCollection<?>> tagCollections = ImmutableBiMap.of();
	private final Map<ResourceLocation, DataTagRegistry<?>> tagRegistries = Maps.newHashMap();
	
	private final Codec<Map<ResourceLocation, TagCollection<?>>> codec = Codec.unboundedMap(ResourceLocation.CODEC, new Codec<>() {
		@Override
		public <T> DataResult<T> encode(TagCollection<?> input, DynamicOps<T> ops, T prefix) {
			return MAP_CODEC.encode(mapCollection(input), ops, prefix);
		}

		private <U> Pair<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>> mapCollection(TagCollection<U> input) {
			ResourceLocation id = getId(input);
			IDataManager<U> manager = DataPackAnvil.WRAPPER.getManager(id);
			Map<ResourceLocation, List<ResourceLocation>> map = Maps.newHashMap();

			input.getAllTags().forEach((key, tag) -> map.put(key, tag.getValues().stream()
					.map(manager::getId)
					.filter(location -> !DataPackAnvilApi.ID_NONE.equals(location))
					.toList()));
			return Pair.of(id, map);
		}

		@Override
		public <T> DataResult<Pair<TagCollection<?>, T>> decode(DynamicOps<T> ops, T input) {
			return MAP_CODEC.decode(ops, input).map(pair -> pair.mapFirst(this::mapToCollection));
		}
		
		private <U> TagCollection<U> mapToCollection(Pair<ResourceLocation, Map<ResourceLocation, List<ResourceLocation>>> input) {
			ResourceLocation id = input.getFirst();
			IDataManager<U> manager = DataPackAnvil.WRAPPER.getManager(id);
			Map<ResourceLocation, Tag<U>> map = Maps.newHashMap();
			
			
			input.getSecond().forEach((key, tag) -> {
				Tag.Builder builder = Tag.Builder.tag();
				
				tag.forEach(location -> builder.addElement(location, key.toString()));
				builder.build(map::get, loc -> manager.getOrDefault(loc, null)).ifRight(builtTag -> map.put(key, builtTag));
			});
			return TagCollection.of(map);
		}

	});
	
	@Override
	public @NotNull Class<TagCollection<?>> getContentType() {
		return (Class<TagCollection<?>>) (Class<?>) TagCollection.class; // double cast or we get a compilation error
	}

	@Override
	public @NotNull Map<ResourceLocation, TagCollection<?>> getData() {
		return tagCollections;
	}

	public boolean shouldLoad() {
		return !tagRegistries.isEmpty();
	}
	
	@Override
	public void setData(@NotNull Map<ResourceLocation, TagCollection<?>> map) {
		tagCollections = ImmutableBiMap.copyOf(map);
		map.forEach(this::injectTagCollection);
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", tagCollections.size(), "DataPack Anvil TagCollection");
	}
	
	private <T> void injectTagCollection(ResourceLocation id, TagCollection<T> collection) {
		DataTagRegistry<T> tagRegistry = (DataTagRegistry<T>) tagRegistries.get(id);
		
		tagRegistry.setCollection(collection);
	}
	
	@Override
	public @NotNull ResourceLocation getId(final TagCollection<?> value) {
		return tagCollections.inverse().getOrDefault(value, DataPackAnvilApi.ID_NONE);
	}
	
	@Override
	public @NotNull String getFolder() {
		return FOLDER;
	}

	public <T> void putTagRegistryFromIMC(Supplier<?> supplier) {
		DataTagIMC<T> imc = (DataTagIMC<T>) supplier.get();
		
		tagRegistries.put(DataPackAnvil.WRAPPER.getId(imc.manager()), imc.registry());
	}
	
	@Override
	public Codec<Map<ResourceLocation, TagCollection<?>>> getCodec() {
		return codec;
	}
	
	@Override
	public @NotNull IDataWrapper<TagCollection<?>> getWrapper(@NotNull ResourceLocation id) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor,
												   @NotNull Executor gameExecutor) {
		List<LoaderInfo<?>> list = Lists.newArrayList();
		
		visitDataManagers(manager -> list.add(this.createLoader(resourceManager, backgroundExecutor, manager)));
		List<CompletableFuture<?>> futures = Lists.newArrayList();
		
		list.stream().map(li -> li.pendingLoad).forEach(futures::add);
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenAcceptAsync(v -> {
			ImmutableMap.Builder<ResourceLocation, TagCollection<?>> builder = ImmutableMap.builder();
			
			list.forEach(loader -> loader.addToBuilder(builder));
			this.setData(builder.build());
		}, gameExecutor);
	}
	
	private void visitDataManagers(Consumer<IDataManager<?>> consumer) {
		DataPackAnvil.WRAPPER.getDataManagers().entrySet().stream()
				.filter(e -> tagRegistries.get(e.getKey()) != null)
				.forEach(e -> consumer.accept(e.getValue()));
	}
	
	private <T> @NotNull LoaderInfo<T> createLoader(ResourceManager resourceManager, Executor executor, IDataManager<T> manager) {
		TagLoader<T> tagLoader = new TagLoader<>(manager::getOptional, FOLDER + manager.getFolder());
		CompletableFuture<? extends TagCollection<T>> completableFuture = CompletableFuture.supplyAsync(() -> tagLoader.loadAndBuild(resourceManager), executor);
			
		return new LoaderInfo<>(manager, completableFuture);
	}

	record LoaderInfo<T>(
			IDataManager<T> manager,
			CompletableFuture<? extends TagCollection<T>> pendingLoad
	) {

		public void addToBuilder(ImmutableMap.Builder<ResourceLocation, TagCollection<?>> builder) {
			builder.put(DataPackAnvil.WRAPPER.getId(manager), this.pendingLoad.join());
		}
	}
}
