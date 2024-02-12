package sirttas.dpanvil.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public abstract class AbstractManagedDataBuilderProvider<T, B> extends AbstractManagedDataProvider<T> {

	private final BiFunction<B, DynamicOps<JsonElement>, JsonElement> builder;
	private final Map<ResourceLocation, B> data;
	private HolderLookup.Provider resolvedRegistries;

	protected AbstractManagedDataBuilderProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, IDataManager<T> manager, Encoder<B> encoder) {
		this(packOutput, registries, manager, (b, o) -> CodecHelper.encode(encoder, o, b));
	}

	protected AbstractManagedDataBuilderProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, IDataManager<T> manager, BiFunction<B, DynamicOps<JsonElement>, JsonElement> builder) {
		super(packOutput, registries, manager);
		this.builder = builder;
		this.data = new HashMap<>();
	}

	@Nonnull
	@Override
	public CompletableFuture<?> run(@Nonnull CachedOutput cache) {
		return registries.thenCompose(r -> {
			resolvedRegistries = r;

			collectBuilders(resolvedRegistries);

			var list = new ArrayList<CompletableFuture<?>>(data.size());
			var ops = RegistryOps.create(JsonOps.INSTANCE, resolvedRegistries);

			for (Map.Entry<ResourceLocation, B> entry : data.entrySet()) {
				list.add(save(cache, ops, entry.getValue(), entry.getKey()));
			}
			data.clear();
			return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
		});
	}

	protected abstract void collectBuilders(HolderLookup.Provider registries);

	protected B add(ResourceKey<T> key, B element) {
		return add(key.location(), element);
	}

	protected B add(ResourceLocation id, B element) {
		return data.compute(id, (k, v) -> {
			if (v != null) {
				throw new IllegalStateException("Duplicate id: " + id + ", manager: " + manager);
			}
			return element;
		});
	}


	protected CompletableFuture<?> save(CachedOutput cache, DynamicOps<JsonElement> ops, B element, ResourceKey<T> key) {
		return save(cache, ops, element, key.location());
	}

	protected CompletableFuture<?> save(CachedOutput cache, DynamicOps<JsonElement> ops, B element, ResourceLocation id) {
		try {
			return save(cache, builder.apply(element, ops), id);
		} catch (Exception e) {
			throw new IllegalStateException("Error saving data: " + id + ", manager: " + manager, e);
		}
	}

	@Nonnull
	protected <U> HolderLookup.RegistryLookup<U> getRegistry(ResourceKey<? extends Registry<U>> registry) {
		return resolvedRegistries.lookupOrThrow(registry);
	}

	public <U> HolderSet.Named<U> createHolderSet(TagKey<U> tag) {
		return HolderSet.emptyNamed(getRegistry(tag.registry()), tag);
	}
}
