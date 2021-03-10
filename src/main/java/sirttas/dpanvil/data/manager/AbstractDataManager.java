package sirttas.dpanvil.data.manager;

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

public abstract class AbstractDataManager<T, U> extends ReloadListener<Map<ResourceLocation, U>> implements IDataManager<T> {

	protected static final Gson GSON = new GsonBuilder().create();
	
	private final Class<T> contentType;
	private final Function<ResourceLocation, T> defaultValueFactory;
	private BiMap<ResourceLocation, T> data = ImmutableBiMap.of();
	protected final String folder;
	protected final BiConsumer<T, ResourceLocation> idSetter;
	protected ResourceLocation id;

	@SuppressWarnings("unchecked")
	public AbstractDataManager(Class<? extends T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		this.contentType = (Class<T>) contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
		this.folder = folder;
	}

	@Override
	public Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public void setData(Map<ResourceLocation, T> map) {
		map.forEach((loc, value) -> idSetter.accept(value, loc));
		data = ImmutableBiMap.copyOf(map);
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
	public String getFolder() {
		return folder;
	}
	
	public void setId(ResourceLocation id) {
		this.id = id;
	}
}
