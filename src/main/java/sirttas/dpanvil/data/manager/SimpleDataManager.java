package sirttas.dpanvil.data.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;


public class SimpleDataManager<T> extends AbstractDataManager<T, JsonElement> {

	public SimpleDataManager(Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		super(contentType, folder, defaultValueFactory, idSetter);
	}

	@Override
	protected Map<ResourceLocation, JsonElement> prepare(IResourceManager resourceManager, IProfiler profilerIn) {
		Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
		int i = this.folder.length() + 1;

		for (ResourceLocation resourcelocation : resourceManager.getAllResourceLocations(this.folder, file -> file.endsWith(".json"))) {
			String path = resourcelocation.getPath();
			ResourceLocation resourceId = new ResourceLocation(resourcelocation.getNamespace(), path.substring(i, path.length() - 5));

			try (IResource resource = resourceManager.getResource(resourcelocation);
					InputStream inputstream = resource.getInputStream();
					Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));) {

				JsonElement jsonelement = JSONUtils.fromJson(GSON, reader, JsonElement.class);

				if (jsonelement != null) {
					map.put(resourceId, jsonelement);
				} else {
					DataPackAnvilApi.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceId, resourcelocation);
				}
			} catch (IllegalArgumentException | IOException | JsonParseException e) {
				DataPackAnvilApi.LOGGER.error("Couldn't parse data file {} from {}", resourceId, resourcelocation, e);
			}
		}
		return map;
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
}