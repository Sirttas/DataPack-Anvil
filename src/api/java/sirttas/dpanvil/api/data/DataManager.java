package sirttas.dpanvil.api.data;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.DataPackAnvilApi;

public abstract class DataManager<T> extends JsonReloadListener {

	private Class<T> clazz;
	private IDataSerializer<T> serializer;
	protected Map<ResourceLocation, T> data = ImmutableMap.of();

	public DataManager(Class<T> clazz, IDataSerializer<T> serializer, String folder) {
		super(new GsonBuilder().registerTypeAdapter(clazz, serializer).create(), folder);
		this.serializer = serializer;
		this.clazz = clazz;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		ImmutableMap.Builder<ResourceLocation, T> builder = ImmutableMap.builder();

		objects.forEach((loc, jsonObject) -> builder.put(loc, gson.fromJson(jsonObject, clazz)));
		data = builder.build();
		refresh();
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), this.getName());
	}

	protected abstract void refresh();
	protected abstract String getName();

	public Map<ResourceLocation, T> getData() {
		return data;
	}

	public void setData(Map<ResourceLocation, T> map) {
		data = ImmutableMap.copyOf(map);
		refresh();
		DataPackAnvilApi.LOGGER.info("Reloaded {} {}", data.size(), this.getName());
	}

	public IDataSerializer<T> getSerializer() {
		return serializer;
	}

}