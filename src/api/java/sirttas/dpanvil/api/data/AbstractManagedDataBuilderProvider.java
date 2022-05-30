package sirttas.dpanvil.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Encoder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractManagedDataBuilderProvider<T, B> extends AbstractManagedDataProvider<T> {

	private final Function<B, JsonElement> builder;

	private final Map<ResourceLocation, B> data;

	protected AbstractManagedDataBuilderProvider(DataGenerator generator, IDataManager<T> manager, Encoder<B> encoder) {
		this(generator, manager, b -> CodecHelper.encode(encoder, b));
	}

	protected AbstractManagedDataBuilderProvider(DataGenerator generator, IDataManager<T> manager, Function<B, JsonElement> builder) {
		super(generator, manager);
		this.builder = builder;
		this.data = new HashMap<>();
	}

	@Override
	public void run(@Nonnull HashCache cache) throws IOException {
		collectBuilders();
		for (Map.Entry<ResourceLocation, B> entry : data.entrySet()) {
			save(cache, entry.getValue(), entry.getKey());
		}
		data.clear();
	}

	protected abstract void collectBuilders();

	protected void add(ResourceKey<T> key, B element) {
		add(key.location(), element);
	}

	protected void add(ResourceLocation id, B element) {
		data.compute(id, (k, v) -> {
			if (v != null) {
				throw new IllegalStateException("Duplicate id: " + id + ", manager: " + manager);
			}
			return element;
		});
	}


	protected void save(HashCache cache, B element, ResourceKey<T> key) throws IOException {
		save(cache, element, key.location());
	}

	protected void save(HashCache cache, B element, ResourceLocation id) throws IOException {
		save(cache, builder.apply(element), id);
	}

}
