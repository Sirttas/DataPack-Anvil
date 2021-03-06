package sirttas.dpanvil.data.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;

public class MergedDataManager<R, T> extends AbstractDataManager<T, List<JsonElement>> {

	private final Function<Stream<R>, T> merger;
	private final Function<JsonElement, R> rawParser;

	public MergedDataManager(Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter, Function<Stream<R>, T> merger,
			Function<JsonElement, R> rawParser) {
		super(contentType, folder, defaultValueFactory, idSetter);
		this.merger = merger;
		this.rawParser = rawParser;
	}

	@Override
	protected Map<ResourceLocation, List<JsonElement>> prepare(IResourceManager resourceManager, IProfiler profilerIn) {
		Map<ResourceLocation, List<JsonElement>> map = Maps.newHashMap();
		int i = this.folder.length() + 1;

		for (ResourceLocation resourcelocation : resourceManager.listResources(this.folder, file -> file.endsWith(".json"))) {
			String path = resourcelocation.getPath();
			ResourceLocation resourceId = new ResourceLocation(resourcelocation.getNamespace(), path.substring(i, path.length() - 5));
			List<JsonElement> list = Lists.newArrayList();

			try {
				for (IResource resource : resourceManager.getResources(resourcelocation)) {
					JsonElement element = getElement(resourcelocation, resourceId, resource);

					if (element != null) {
						if (element instanceof JsonObject) {
							JsonObject json = (JsonObject) element;
							
							if (json.has(DPAnvilNames.REPLACE) && json.get(DPAnvilNames.REPLACE).getAsBoolean()) {
								list.clear();
							}
						}
						list.add(element);
					} else {
						DataPackAnvilApi.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceId, resourcelocation);
					}
				}
			} catch (IOException e) {
				DataPackAnvilApi.LOGGER.error("Couldn't parse data file {} from {}", resourceId, resourcelocation, e);
			}
			map.put(resourceId, list);
		}
		return map;
	}

	public static JsonElement getElement(ResourceLocation resourcelocation, ResourceLocation resourceId, IResource resource) {
		try (InputStream inputstream = resource.getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));) {
			return JSONUtils.fromJson(GSON, reader, JsonElement.class);
		} catch (IllegalArgumentException | IOException | JsonParseException e) {
			DataPackAnvilApi.LOGGER.error("Couldn't parse data file {} from {}", resourceId, resourcelocation, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void apply(Map<ResourceLocation, List<JsonElement>> objects, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		Map<ResourceLocation, T> map = Maps.newHashMap();
		Function<JsonElement, R> parser = rawParser != null ? rawParser : json -> (R) DataPackAnvil.WRAPPER.getSerializer(id).read(json);

		objects.forEach((loc, list) -> {
			T value = merger.apply(list.stream().map(parser));
		
			idSetter.accept(value, loc);
			map.put(loc, value);
		});
		setData(map);
	}
}
