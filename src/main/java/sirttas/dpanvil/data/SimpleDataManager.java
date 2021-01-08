package sirttas.dpanvil.data;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;


public class SimpleDataManager<T> extends JsonReloadListener implements IDataManager<T> {

	private static final Gson GSON = new GsonBuilder().create();

	private final Class<T> contentType;
	ResourceLocation id;
	private Map<ResourceLocation, T> data = ImmutableMap.of();

	public SimpleDataManager(Class<T> contentType, String folder) {
		super(GSON, folder);
		this.contentType = contentType;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		Map<ResourceLocation, T> map = Maps.newHashMap();
		IJsonDataSerializer<T> serializer = DataPackAnvil.WRAPPER.getSerializer(id);

		objects.forEach((loc, jsonObject) -> map.put(loc, serializer.read(jsonObject)));
		setData(map);
	}

	@Override
	public Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public void setData(Map<ResourceLocation, T> map) {
		data = ImmutableMap.copyOf(map);
		MinecraftForge.EVENT_BUS.post(new DataManagerReloadEvent<>(this));
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), id);
	}

	@Override
	public Class<T> getContentType() {
		return contentType;
	}


}