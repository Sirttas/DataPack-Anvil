package sirttas.dpanvil.data.manager;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataManager.Builder;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractBuilder<T> implements IDataManager.Builder<T> {

	protected final Class<T> type;
	protected final ResourceKey<IDataManager<T>> key;
	protected String folder;
	protected Function<ResourceLocation, T> defaultValueFactory = id -> null;
	protected BiConsumer<T, ResourceLocation> idSetter = (t, id) -> {};

	protected AbstractBuilder(Class<T> type, @Nonnull ResourceKey<IDataManager<T>> key) {
		this.type = type;
		this.key = key;

		var location = key.location();

		this.folder = location.getNamespace() + "/" + location.getPath();
	}

	@Override
	public Builder<T> withDefault(Function<ResourceLocation, T> factory) {
		this.defaultValueFactory = factory;
		return this;
	}

	@Override
	public Builder<T> folder(String folder) {
		this.folder = folder;
		return this;
	}

	@Override
	public Builder<T> withIdSetter(BiConsumer<T, ResourceLocation> idSetter) {
		this.idSetter = idSetter;
		return this;
	}
	
	@Override
	public <R> Builder<T> merged(Function<Stream<R>, T> merger, Function<JsonElement, R> rawParser) {
		return new MergedDataManagerBuilder<>(this, merger, rawParser);
	}

}
