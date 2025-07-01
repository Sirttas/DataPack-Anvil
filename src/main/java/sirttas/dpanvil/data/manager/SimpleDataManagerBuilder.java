package sirttas.dpanvil.data.manager;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.stream.Stream;

public class SimpleDataManagerBuilder<T> extends AbstractBuilder<T> {

	protected boolean hasInheritance;

	public SimpleDataManagerBuilder(Class<T> type, @Nonnull ResourceKey<IDataManager<T>> key) {
		super(type, key);
		hasInheritance = false;
	}

	public IDataManager.Builder<T> withInheritance() {
		hasInheritance = true;
		return this;
	}

	@Override
	public <R> IDataManager.Builder<T> merged(Function<Stream<R>, T> merger, Function<JsonElement, R> rawParser) {
		if (hasInheritance) {
			throw new UnsupportedOperationException("Inheritance is not supported for merged data managers.");
		}
		return super.merged(merger, rawParser);
	}

	@Override
	public IDataManager<T> build() {
		return new SimpleDataManager<>(key, type, folder, defaultValueFactory, idSetter, hasInheritance);
	}
}
