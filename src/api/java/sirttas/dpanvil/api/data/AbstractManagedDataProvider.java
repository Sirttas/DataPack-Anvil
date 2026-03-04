package sirttas.dpanvil.api.data;

import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractManagedDataProvider<T> implements DataProvider {
	
	protected final PackOutput packOutput;
	protected final CompletableFuture<HolderLookup.Provider> registries;
	protected final IDataManager<T> manager;

	protected AbstractManagedDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, IDataManager<T> manager) {
		this.packOutput = packOutput;
		this.registries = registries;
		this.manager = manager;
	}
	
	protected CompletableFuture<?> save(CachedOutput cache, JsonElement element, ResourceKey<T> id) {
		return save(cache, element, id.identifier());
	}

	protected CompletableFuture<?> save(CachedOutput cache, JsonElement element, Identifier id) {
		return DataProvider.saveStable(cache, element, getPath(id));
	}

	private Path getPath(Identifier id) {
		return this.packOutput.getOutputFolder().resolve("data/" + id.getNamespace() + "/" + manager.getFolder() + "/" + id.getPath() + ".json");
	}
}
