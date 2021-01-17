package sirttas.dpanvil.data;

import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;


public class SimpleDataManager<T> extends JsonReloadListener implements IDataManager<T> {

	private static final Gson GSON = new GsonBuilder().create();

	private final Class<T> contentType;
	ResourceLocation id;
	private BiMap<ResourceLocation, T> data = ImmutableBiMap.of();
	private T defaultValue;
	private BiConsumer<T, ResourceLocation> idSetter;

	public SimpleDataManager(Class<T> contentType, String folder) {
		super(GSON, folder);
		this.contentType = contentType;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		Map<ResourceLocation, T> map = Maps.newHashMap();
		IJsonDataSerializer<T> serializer = DataPackAnvil.WRAPPER.getSerializer(id);

		objects.forEach((loc, jsonObject) -> {
			T value = serializer.read(jsonObject);
		
			if (idSetter != null) {
				idSetter.accept(value, loc);
			}
			map.put(loc, value);
		});
		setData(map);
	}

	@Override
	public Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public void setData(Map<ResourceLocation, T> map) {
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
		if (defaultValue == null) {
			return data.get(id);
		}
		return data.getOrDefault(id, defaultValue);
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setIdSetter(BiConsumer<T, ResourceLocation> idSetter) {
		this.idSetter = idSetter;
	}

}