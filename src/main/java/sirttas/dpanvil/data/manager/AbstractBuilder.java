package sirttas.dpanvil.data.manager;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataManager.Builder;

public abstract class AbstractBuilder<T> implements IDataManager.Builder<T> {

	protected final Class<T> type;
	protected final String folder;
	protected Function<ResourceLocation, T> defaultValueFactory = id -> null;
	protected BiConsumer<T, ResourceLocation> idSetter = (t, id) -> {};

	protected AbstractBuilder(Class<T> type, String folder) {
		this.type = type;
		this.folder = folder;
	}

	@Override
	public Builder<T> withDefault(Function<ResourceLocation, T> factory) {
		this.defaultValueFactory = factory;
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