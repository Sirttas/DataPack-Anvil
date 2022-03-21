package sirttas.dpanvil.data.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataWrapper;

import javax.annotation.Nonnull;

public abstract class AbstractDataManager<T, U> extends ReloadListener<Map<ResourceLocation, U>> implements IDataManager<T> {

	protected static final Gson GSON = new GsonBuilder().create();
	
	private final Class<T> contentType;
	private final Function<ResourceLocation, T> defaultValueFactory;
	private final Map<ResourceLocation, DefaultDataWrapper<T>> wrappers;
	private BiMap<ResourceLocation, T> data;
	protected final String folder;
	protected final BiConsumer<T, ResourceLocation> idSetter;
	protected ResourceLocation id;

	protected AbstractDataManager(Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		this.contentType = contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
		this.folder = folder;
		this.data = ImmutableBiMap.of();
		this.wrappers = new HashMap<>();
	}

	@Override
	public Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public void setData(Map<ResourceLocation, T> map) {
		map.forEach((loc, value) -> idSetter.accept(value, loc));
		data = ImmutableBiMap.copyOf(map);
		this.wrappers.values().forEach(w -> w.set(this.get(w.getId())));
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), id);
	}

	@Override
	public Class<T> getContentType() {
		return contentType;
	}

	@Override
	public ResourceLocation getId(final T value) {
		return data.inverse().getOrDefault(value, DataPackAnvilApi.ID_NONE);
	}

	@Override
	public T get(ResourceLocation id) {
		T value = data.get(id);

		if (value != null) {
			return value;
		}
		return defaultValueFactory.apply(id);
	}

	@Override
	public @Nonnull
	IDataWrapper<T> getWrapper(@Nonnull ResourceLocation id) {
		return this.wrappers.computeIfAbsent(id, i -> {
			DefaultDataWrapper<T> wrapper = new DefaultDataWrapper<>(id);

			wrapper.set(this.get(id));
			return wrapper;
		});
	}

	@Override
	public String getFolder() {
		return folder;
	}
	
	public void setId(ResourceLocation id) {
		this.id = id;
	}
}
