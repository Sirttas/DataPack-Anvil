package sirttas.dpanvil.api.data;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractManagedDataProvider<T> implements DataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	protected final DataGenerator generator;
	protected final IDataManager<T> manager;

	protected AbstractManagedDataProvider(DataGenerator generator, IDataManager<T> manager) {
		this.generator = generator;
		this.manager = manager;
	}

	protected void save(HashCache cache, JsonElement element, IDataWrapper<T> wrapper) throws IOException {
		save(cache, element, wrapper.getId());
	}
	
	protected void save(HashCache cache, JsonElement element, ResourceLocation id) throws IOException {
		DataProvider.save(GSON, cache, element, getPath(id));
	}

	private Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/" + manager.getFolder() + "/" + id.getPath() + ".json");
	}
}
