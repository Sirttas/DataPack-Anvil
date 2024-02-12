package sirttas.dpanvil.data.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.registry.RegistryListener;

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

public class MergedDataManager<R, T> extends AbstractDataManager<T, List<JsonElement>> {

	private final Function<Stream<R>, T> merger;
	private final Function<JsonElement, R> rawParser;

	public MergedDataManager(ResourceKey<IDataManager<T>> key, Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter, Function<Stream<R>, T> merger,
							 Function<JsonElement, R> rawParser) {
		super(key, contentType, folder, defaultValueFactory, idSetter);
		this.merger = merger;
		this.rawParser = rawParser;
	}

	@Override
	protected @NotNull Map<ResourceLocation, List<JsonElement>> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		Map<ResourceLocation, List<JsonElement>> map = Maps.newHashMap();
		int i = this.folder.length() + 1;

		for (var entry : resourceManager.listResourceStacks(this.folder, file -> file.getPath().endsWith(".json")).entrySet()) {
			var resourceLocation = entry.getKey();
			String path = resourceLocation.getPath();
			ResourceLocation resourceId = new ResourceLocation(resourceLocation.getNamespace(), path.substring(i, path.length() - 5));
			List<JsonElement> list = Lists.newArrayList();

			for (Resource resource : entry.getValue()) {
				JsonElement element = getElement(resourceLocation, resourceId, resource);

				if (element != null) {
					if (element instanceof JsonObject json && json.has(DPAnvilNames.REPLACE) && json.get(DPAnvilNames.REPLACE).getAsBoolean()) {
						list.clear();
					}
					list.add(element);
				} else {
					DataPackAnvilApi.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceId, resourceLocation);
				}
			}
			map.put(resourceId, list);
		}
		return map;
	}

	public static JsonElement getElement(ResourceLocation resourcelocation, ResourceLocation resourceId, Resource resource) {
		try (InputStream inputstream = resource.open(); Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
			return GsonHelper.fromJson(GSON, reader, JsonElement.class);
		} catch (IllegalArgumentException | IOException | JsonParseException e) {
			DataPackAnvilApi.LOGGER.error("Couldn't parse data file {} from {}", resourceId, resourcelocation, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void apply(@NotNull Map<ResourceLocation, List<JsonElement>> objects, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		RegistryListener.getInstance().listen(r -> {
			try {
				Map<ResourceLocation, T> map = Maps.newHashMap();
				Function<JsonElement, R> parser = rawParser != null ? rawParser : json -> (R) DataPackAnvil.WRAPPER.getSerializer(key).read(json);

				objects.forEach((loc, list) -> {
					T value = merger.apply(list.stream().map(parser));

					idSetter.accept(value, loc);
					map.put(loc, value);
				});
				setData(map);
			} catch (Exception e) {
				DataManagerWrapper.logManagerException(key, e);
			}
		});
	}
}
