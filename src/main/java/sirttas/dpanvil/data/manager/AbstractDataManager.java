package sirttas.dpanvil.data.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.neoforged.neoforge.common.NeoForge;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractDataManager<T, U> extends SimplePreparableReloadListener<Map<ResourceLocation, U>> implements IDataManager<T> {

	protected static final Gson GSON = new GsonBuilder().create();
	
	private final Class<T> contentType;
	private final Function<ResourceLocation, T> defaultValueFactory;
	private final Map<ResourceLocation, Holder.Reference<T>> references;
	private BiMap<ResourceLocation, T> data;
	private Map<ResourceLocation, T> remapedData;
	protected final String folder;
	protected final BiConsumer<T, ResourceLocation> idSetter;
	protected final ResourceKey<IDataManager<T>> key;
	
	protected AbstractDataManager(ResourceKey<IDataManager<T>> key, Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		this.key = key;
		this.contentType = contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
		this.folder = folder;
		this.data = ImmutableBiMap.of();
		this.references = new HashMap<>();
		this.remapedData = Collections.emptyMap();
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
			remapedData = Map.copyOf(remap);
		} else {
			remapedData = Collections.emptyMap();
		}
		data = ImmutableBiMap.copyOf(map);

		rebindReferences();
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), key);
		NeoForge.EVENT_BUS.post(new DataManagerReloadEvent<>(this));
	}

	@Override
	public @Nonnull Class<T> getContentType() {
		return contentType;
	}

	@Override
	public @Nonnull ResourceLocation getId(final T value) {
		return data.inverse().getOrDefault(value, DataPackAnvilApi.ID_NONE);
	}

	@Override
	public T get(@Nonnull ResourceLocation id) {
		T value = data.get(id);

		if (value != null) {
			return value;
		}

		value = remapedData.get(id);
		if (value != null) {
			return value;
		}
		return defaultValueFactory.apply(id);
	}

	@Override
	@Nonnull
	public  Holder<T> getOrCreateHolder(@Nonnull ResourceKey<T> key) {
		synchronized (this.references) {
			return this.references.computeIfAbsent(key.location(), i -> {
				var reference = Holder.Reference.createStandAlone(this, key);

				if (data.containsKey(i)) {
					reference.bindValue(data.get(i));
				}
				return reference;
			});
		}
	}

	@Override
	@Nonnull
	public  Holder<T> getOrCreateHolder(@Nonnull ResourceLocation key) {
		return getOrCreateHolder(createKey(key));
	}
	
	@Override
	public @Nonnull String getFolder() {
		return folder;
	}

	private void rebindReferences() {
		synchronized (this.references) {
			this.references.values().forEach(r -> r.bindValue(this.get(r.key().location())));
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
}
