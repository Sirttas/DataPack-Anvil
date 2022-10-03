package sirttas.dpanvil.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractManagedDataBuilderProvider<T, B> extends AbstractManagedDataProvider<T> {

	private final Function<B, JsonElement> builder;
	private final Map<ResourceLocation, B> data;
	private static final RegistryOps<JsonElement> REGISTRY_OPS = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());

	protected AbstractManagedDataBuilderProvider(DataGenerator generator, IDataManager<T> manager, Encoder<B> encoder) {
		this(generator, manager, b -> CodecHelper.encode(encoder, REGISTRY_OPS, b));
	}

	protected AbstractManagedDataBuilderProvider(DataGenerator generator, IDataManager<T> manager, Function<B, JsonElement> builder) {
		super(generator, manager);
		this.builder = builder;
		this.data = new HashMap<>();
	}

	@Override
	public void run(@Nonnull CachedOutput cache) throws IOException {
		collectBuilders();
		for (Map.Entry<ResourceLocation, B> entry : data.entrySet()) {
			save(cache, entry.getValue(), entry.getKey());
		}
		data.clear();
	}

	protected abstract void collectBuilders();

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


	protected void save(CachedOutput cache, B element, ResourceKey<T> key) throws IOException {
		save(cache, element, key.location());
	}

	protected void save(CachedOutput cache, B element, ResourceLocation id) throws IOException {
		try {
			save(cache, builder.apply(element), id);
		} catch (Exception e) {
			throw new IllegalStateException("Error saving data: " + id + ", manager: " + manager, e);
		}
	}

	@Nonnull
	protected <U> Registry<U> getRegistry(ResourceKey<? extends Registry<U>> registry) {
		return REGISTRY_OPS.registry(registry).orElseThrow(() -> new IllegalStateException("Registry " + registry + " not found"));
	}

	public <U> HolderSet<U> createHolderSet(TagKey<U> tag) {
		return getRegistry(tag.registry()).getOrCreateTag(tag);
	}

}
