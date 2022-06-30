package sirttas.dpanvil.api.data;

import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractManagedDataProvider<T> implements DataProvider {
	
	protected final DataGenerator generator;
	protected final IDataManager<T> manager;

	protected AbstractManagedDataProvider(DataGenerator generator, IDataManager<T> manager) {
		this.generator = generator;
		this.manager = manager;
	}
	
	protected void save(CachedOutput cache, JsonElement element, ResourceKey<T> id) throws IOException {
		save(cache, element, id.location());
	}

	protected void save(CachedOutput cache, JsonElement element, ResourceLocation id) throws IOException {
		DataProvider.saveStable(cache, element, getPath(id));
	}

	private Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/" + manager.getFolder() + "/" + id.getPath() + ".json");
	}
}
