package sirttas.dpanvil.data;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
	private final Function<ResourceLocation, T> defaultValueFactory;
	private final BiConsumer<T, ResourceLocation> idSetter;
	ResourceLocation id;
	private BiMap<ResourceLocation, T> data = ImmutableBiMap.of();

	public SimpleDataManager(Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		super(GSON, folder);
		this.contentType = contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		Map<ResourceLocation, T> map = Maps.newHashMap();
		IJsonDataSerializer<T> serializer = DataPackAnvil.WRAPPER.getSerializer(id);

		objects.forEach((loc, jsonObject) -> {
			T value = serializer.read(jsonObject);
		
			idSetter.accept(value, loc);
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
}