package sirttas.dpanvil.data.manager;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.preprocessor.DataPreprocessor;
import sirttas.dpanvil.api.data.preprocessor.NeoForgeConditionsPreprocessor;
import sirttas.dpanvil.api.data.preprocessor.PickingDataPreprocessor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataManagerBuilder<T> implements IDataManager.Builder<T> {

	private final Class<T> type;
	private final ResourceKey<IDataManager<T>> key;
	private final List<DataPreprocessor> preprocessors;

	private String folder;
	private Function<Identifier, T> defaultValueFactory = id -> null;
	private BiConsumer<T, Identifier> idSetter = (t, id) -> {};

	public DataManagerBuilder(Class<T> type, @Nonnull ResourceKey<IDataManager<T>> key) {
		this.type = type;
		this.key = key;
		this.preprocessors = new ArrayList<>();

		var location = key.identifier();

		this.folder = location.getNamespace() + "/" + location.getPath();
	}

	@Override
	public IDataManager.Builder<T> withDefault(Function<Identifier, T> factory) {
		this.defaultValueFactory = factory;
		return this;
	}

	@Override
	public IDataManager.Builder<T> preprocessor(DataPreprocessor preprocessor) {
		preprocessors.add(preprocessor);
		return this;
	}

	@Override
	public IDataManager.Builder<T> defaultPreprocessors() {
		preprocessors.add(new NeoForgeConditionsPreprocessor());
		preprocessors.add(new PickingDataPreprocessor());
		return this;
	}

	@Override
	public IDataManager.Builder<T> folder(String folder) {
		this.folder = folder;
		return this;
	}

	@Override
	public IDataManager.Builder<T> idSetter(BiConsumer<T, Identifier> idSetter) {
		this.idSetter = idSetter;
		return this;
	}


	@Override
	public IDataManager<T> build() {
		if (preprocessors.isEmpty()) {
			defaultPreprocessors();
		}
		return new DataManager<>(key, type, folder, defaultValueFactory, idSetter, preprocessors);
	}
}
