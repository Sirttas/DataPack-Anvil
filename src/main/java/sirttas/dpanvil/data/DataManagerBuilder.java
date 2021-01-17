package sirttas.dpanvil.data;

import java.util.function.BiConsumer;

import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.IDataManager;

public class DataManagerBuilder<T> implements IDataManager.Builder<T> {

	private final Class<T> type;
	private final String folder;
	private T defaultValue;
	private BiConsumer<T, ResourceLocation> idSetter;

	public DataManagerBuilder(Class<T> type, String folder) {
		this.type = type;
		this.folder = folder;
	}

	@Override
	public DataManagerBuilder<T> withIdSetter(BiConsumer<T, ResourceLocation> idSetter) {
		this.idSetter = idSetter;
		return this;
	}

	@Override
	public DataManagerBuilder<T> withDefault(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public IDataManager<T> build() {
		SimpleDataManager<T> manager = new SimpleDataManager<>(type, folder);

		if (defaultValue != null) {
			manager.setDefaultValue(defaultValue);
		}
		if (idSetter != null) {
			manager.setIdSetter(idSetter);
		}
		return manager;
	}

}
