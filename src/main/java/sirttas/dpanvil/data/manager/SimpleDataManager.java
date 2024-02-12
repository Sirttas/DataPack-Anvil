package sirttas.dpanvil.data.manager;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;
import sirttas.dpanvil.registry.RegistryListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;


public class SimpleDataManager<T> extends AbstractDataManager<T, JsonElement> {

	public SimpleDataManager(ResourceKey<IDataManager<T>> key, Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		super(key, contentType, folder, defaultValueFactory, idSetter);
	}

	@Override
	protected @NotNull Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
		int i = this.folder.length() + 1;

		for (var entry : resourceManager.listResources(this.folder, file -> file.getPath().endsWith(".json")).entrySet()) {
			var resourceLocation = entry.getKey();
			String path = resourceLocation.getPath();
			ResourceLocation resourceId = new ResourceLocation(resourceLocation.getNamespace(), path.substring(i, path.length() - 5));


			try (InputStream inputstream = entry.getValue().open()) {
				Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
				JsonElement jsonelement = GsonHelper.fromJson(GSON, reader, JsonElement.class);

				if (jsonelement != null) {
					map.put(resourceId, jsonelement);
				} else {
					DataPackAnvilApi.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceId, resourceLocation);
				}
			} catch (IOException e) {
				DataPackAnvilApi.LOGGER.error("Couldn't parse data file {} from {}", resourceId, resourceLocation, e);
			}
		}
		return map;
	}

	
	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		RegistryListener.getInstance().listen(r -> {
			try {
				Map<ResourceLocation, T> map = Maps.newHashMap();
				IJsonDataSerializer<T, ?> serializer = DataPackAnvil.WRAPPER.getSerializer(key);

				objects.forEach((loc, jsonObject) -> {
					try {
						T value = serializer.read(jsonObject);

						idSetter.accept(value, loc);
						map.put(loc, value);
					} catch (Exception e) {
						throw new RuntimeException("Failed to load data file " + loc, e);
					}
				});
				setData(map);
			} catch (Exception e) {
				DataManagerWrapper.logManagerException(key, e);
			}
		});
	}
}
