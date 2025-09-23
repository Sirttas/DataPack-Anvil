package sirttas.dpanvil.data.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.preprocessor.DataPreprocessor;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;
import sirttas.dpanvil.registry.RegistryListener;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataManager<T> extends SimplePreparableReloadListener<Map<ResourceLocation, List<JsonElement>>> implements IDataManager<T> {

	private static final Gson GSON = new GsonBuilder().create();

	private final Class<T> contentType;
	private final Function<ResourceLocation, T> defaultValueFactory;
	private final Map<ResourceLocation, Holder.Reference<T>> references;
	private final String folder;
	private final BiConsumer<T, ResourceLocation> idSetter;
	private final ResourceKey<IDataManager<T>> key;
	private final List<DataPreprocessor> preprocessors;
	private Map<ResourceLocation, T> data;
	private Map<ResourceLocation, T> remappedData;
	private PreprocessorContext preprocessorContext;

	public DataManager(ResourceKey<IDataManager<T>> key, Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter, List<DataPreprocessor> preprocessors) {
		this.key = key;
		this.contentType = contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
		this.folder = folder;
		this.data = ImmutableBiMap.of();
		this.references = new HashMap<>();
		this.remappedData = Collections.emptyMap();
		this.preprocessors = preprocessors;
		if (preprocessors.isEmpty()) {
			DataPackAnvilApi.LOGGER.warn("No preprocessors provided for {}. This may lead to unexpected behavior.", key.location());
		}
		this.preprocessorContext = new PreprocessorContext(Map.of());
	}

	@Override
	public @Nonnull Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public ResourceKey<IDataManager<T>> getKey() {
		return key;
	}

	@Override
	public void setData(@Nonnull Map<ResourceLocation, T> map) {
		map.forEach((loc, value) -> idSetter.accept(value, loc));
		if (this != DataPackAnvilApi.REMAP_KEYS_MANAGER) {
			var remap = new HashMap<ResourceLocation, T>();

			DataPackAnvilApi.REMAP_KEYS_MANAGER.get(this.key.location()).keys().forEach((k, v) -> {
				var value = map.get(v);

				if (value != null) {
					remap.put(k, value);
				}
			});
			remappedData = Map.copyOf(remap);
		} else {
			remappedData = Collections.emptyMap();
		}
		try {
			data = ImmutableBiMap.copyOf(map);
		} catch (IllegalArgumentException e) {
			DataPackAnvilApi.LOGGER.warn("Manager {} has duplicate values ({}), by key search will be slower and may be inconsistent", () -> key, e::getMessage);
			data = Map.copyOf(map);
		}

		rebindReferences();
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), key);
		NeoForge.EVENT_BUS.post(new DataManagerReloadEvent(this));
	}

	@Override
	public @Nonnull Class<T> getContentType() {
		return contentType;
	}

	@Override
	public @Nonnull ResourceLocation getId(final T value) {
		if (data instanceof BiMap) {
			return ((BiMap<ResourceLocation, T>) data).inverse().getOrDefault(value, DPAnvilNames.ResourceLocations.NONE);
		}
		for (var entry : data.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return DPAnvilNames.ResourceLocations.NONE;
	}

	@Override
	public T get(@Nonnull ResourceLocation id) {
		T value = data.get(id);

		if (value != null) {
			return value;
		}

		value = remappedData.get(id);
		if (value != null) {
			return value;
		}
		return defaultValueFactory.apply(id);
	}

	@Override
	@Nonnull
	public Holder<T> getOrCreateHolder(@Nonnull ResourceKey<T> key) {
		synchronized (this.references) {
			return this.references.computeIfAbsent(key.location(), resourceLocation -> {
				var reference = Holder.Reference.createStandAlone(this, key);

				bindReference(reference, resourceLocation);
				return reference;
			});
		}
	}

	@Override
	@Nonnull
	public Holder<T> getOrCreateHolder(@Nonnull ResourceLocation key) {
		return getOrCreateHolder(createKey(key));
	}

	@Override
	public @Nonnull String getFolder() {
		return folder;
	}

	private void rebindReferences() {
		synchronized (this.references) {
			this.references.values().forEach(r -> bindReference(r, r.key().location()));
		}
	}

	private void bindReference(Holder.Reference<T> reference, ResourceLocation resourceLocation) {
		var value = this.get(resourceLocation);

		if (value == null) {
			DataPackAnvilApi.LOGGER.debug("Could not bind reference for {} in manager {}", resourceLocation, key);
			return;
		}
		reference.bindValue(value);
		if (!reference.isBound()) {
			DataPackAnvilApi.LOGGER.warn("Failed to bind reference {} for manager {}", resourceLocation, key);
		}
	}

	@Nonnull
	private ResourceKey<T> createKey(ResourceLocation l) {
		return IDataManager.createKey(this.key, l);
	}

	@Override
	public String toString() {
		return key != null ? key.toString() : folder;
	}

	public JsonElement preprocess(JsonElement input) {
		return preprocessorContext.processAnonymous(input);
	}

	@Override
	protected @NotNull Map<ResourceLocation, List<JsonElement>> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		var map = new HashMap<ResourceLocation, List<JsonElement>>();
		var i = this.folder.length() + 1;

		for (var entry : resourceManager.listResourceStacks(this.folder, file -> file.getPath().endsWith(".json")).entrySet()) {
			var resourceLocation = entry.getKey();
			var path = resourceLocation.getPath();
			var resourceId = ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), path.substring(i, path.length() - 5));
			var list = new ArrayList<JsonElement>();

			for (var resource : entry.getValue()) {
				JsonElement element = getElement(resourceLocation, resourceId, resource);

				if (element == null) {
					continue;
				}
				list.add(element);
			}
			map.put(resourceId, list);
		}
		preprocessorContext = new PreprocessorContext(map);
		return map;
	}

	private static JsonElement getElement(ResourceLocation resourcelocation, ResourceLocation resourceId, Resource resource) {
		try (var inputstream = resource.open();
			 var reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
			return GsonHelper.fromJson(GSON, reader, JsonElement.class);
		} catch (IllegalArgumentException | IOException | JsonParseException e) {
			DataPackAnvilApi.LOGGER.error("Couldn't parse data file {} from {}", resourceId, resourcelocation, e);
		}
		return null;
	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, List<JsonElement>> objects, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		RegistryListener.getInstance().listen(r -> {
			try {
				while (!preprocessorContext.rawData.isEmpty()) {
					preprocessorContext.process(preprocessorContext.rawData.keySet().iterator().next());
				}

				Map<ResourceLocation, T> map = Maps.newHashMap();
				IJsonDataSerializer<T, ?> serializer = DataPackAnvil.WRAPPER.getSerializer(key);

				preprocessorContext.processedData.forEach((loc, jsonElements) -> {
					try {
						T value = serializer.read(jsonElements.getFirst());

						idSetter.accept(value, loc);
						map.put(loc, value);
					} catch (Exception e) {
						throw new RuntimeException("Failed to load data file " + loc, e);
					}
				});
				setData(map);
			} catch (Exception e) {
				DataManagerWrapper.logManagerException(key, e);
			}
		});
	}

	private class PreprocessorContext implements DataPreprocessor.Context {

		private final Map<ResourceLocation, List<JsonElement>> rawData;
		private final Map<ResourceLocation, List<JsonElement>> processedData;
		private final List<ResourceLocation> processingIds;

        private PreprocessorContext(Map<ResourceLocation, List<JsonElement>> rawData) {
			this.rawData = new HashMap<>(rawData);
			this.processedData = Maps.newHashMap();
			processingIds = Lists.newArrayList();
        }

		@Override
		public HolderLookup.Provider getRegistryLookup() {
			return DataManager.this.getRegistryLookup();
		}

		@Override
		public ICondition.IContext getConditionContext() {
			return getContext();
		}

		@Override
		public List<JsonElement> getProcessed(ResourceLocation id) {
			if (processedData.containsKey(id)) {
				return processedData.get(id);
			} else if (processingIds.contains(id)) {
				throw new IllegalStateException("Data for " + id + " is already being processed. This is likely a circular dependency.");
			}
			process(id);
			return processedData.get(id);
		}

		void process(ResourceLocation id) {
			List<JsonElement> elements = rawData.remove(id);
			processingIds.add(id);

			for (DataPreprocessor preprocessor : preprocessors) {
				elements = preprocessor.preprocess(this, elements);

				if (elements == null || elements.isEmpty()) {
					break;
				}
			}
			processingIds.remove(id);
			if (elements == null || elements.isEmpty()) {
				return;
			}
			processedData.put(id, elements);
		}

		JsonElement processAnonymous(JsonElement element) {
			var list = List.of(element);

			for (DataPreprocessor preprocessor : preprocessors) {
				list = preprocessor.preprocess(this, list);

				if (list == null || list.isEmpty()) {
					break;
				}
			}
			return list != null && !list.isEmpty() ? list.getFirst() : JsonNull.INSTANCE;
		}
	}
}
