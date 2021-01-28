package sirttas.dpanvil.data;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataManager.Builder;

public class DataManagerBuilder<T> implements IDataManager.Builder<T> {

	private final Class<T> type;
	private final String folder;
	private Function<ResourceLocation, T> defaultValueFactory = id -> null;
	private BiConsumer<T, ResourceLocation> idSetter = (t, id) -> {};

	public DataManagerBuilder(Class<T> type, String folder) {
		this.type = type;
		this.folder = folder;
	}

	@Override
	public Builder<T> withDefault(Function<ResourceLocation, T> factory) {
		this.defaultValueFactory = factory;
		return this;
	}

	@Override
	public DataManagerBuilder<T> withIdSetter(BiConsumer<T, ResourceLocation> idSetter) {
		this.idSetter = idSetter;
		return this;
	}

	@Override
	public IDataManager<T> build() {
		return new SimpleDataManager<>(type, folder, defaultValueFactory, idSetter);
	}
}
