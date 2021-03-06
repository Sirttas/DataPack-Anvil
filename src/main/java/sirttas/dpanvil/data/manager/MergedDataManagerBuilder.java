package sirttas.dpanvil.data.manager;

import java.util.function.Function;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

import sirttas.dpanvil.api.data.IDataManager;

public class MergedDataManagerBuilder<R, T> extends AbstractBuilder<T> {

	private Function<Stream<R>, T> merger;
	private Function<JsonElement, R> rawParser;

	public MergedDataManagerBuilder(AbstractBuilder<T> source, Function<Stream<R>, T> merger, Function<JsonElement, R> rawParser) {
		super(source.type, source.folder);
		this.defaultValueFactory = source.defaultValueFactory;
		this.idSetter = source.idSetter;
		this.merger = merger;
		this.rawParser = rawParser;
	}
	
	@Override
	public IDataManager<T> build() {
		return new MergedDataManager<>(type, folder, defaultValueFactory, idSetter, merger, rawParser);
	}
}
