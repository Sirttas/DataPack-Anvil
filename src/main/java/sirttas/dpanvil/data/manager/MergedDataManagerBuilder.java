package sirttas.dpanvil.data.manager;

import com.google.gson.JsonElement;
import sirttas.dpanvil.api.data.IDataManager;

import java.util.function.Function;
import java.util.stream.Stream;

public class MergedDataManagerBuilder<R, T> extends AbstractBuilder<T> {

	private final Function<Stream<R>, T> merger;
	private final Function<JsonElement, R> rawParser;

	public MergedDataManagerBuilder(AbstractBuilder<T> source, Function<Stream<R>, T> merger, Function<JsonElement, R> rawParser) {
		super(source.type, source.key);
		this.defaultValueFactory = source.defaultValueFactory;
		this.idSetter = source.idSetter;
		this.merger = merger;
		this.rawParser = rawParser;
		this.folder = source.folder;
	}
	
	@Override
	public IDataManager<T> build() {
		return new MergedDataManager<>(key, type, folder, defaultValueFactory, idSetter, merger, rawParser);
	}
}
