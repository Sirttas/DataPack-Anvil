package sirttas.dpanvil.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractManagedDataProvider<T> implements DataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	protected final DataGenerator generator;
	protected final IDataManager<T> manager;

	protected AbstractManagedDataProvider(DataGenerator generator, IDataManager<T> manager) {
		this.generator = generator;
		this.manager = manager;
	}

	@Deprecated(since = "1.18.2-3.3.3", forRemoval = true)
	protected void save(HashCache cache, JsonElement element, IDataWrapper<T> wrapper) throws IOException {
		save(cache, element, wrapper.getId());
	}
	
	protected void save(HashCache cache, JsonElement element, ResourceKey<T> id) throws IOException {
		save(cache, element, id.location());
	}

	protected void save(HashCache cache, JsonElement element, ResourceLocation id) throws IOException {
		DataProvider.save(GSON, cache, element, getPath(id));
	}

	private Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/" + manager.getFolder() + "/" + id.getPath() + ".json");
	}
}
