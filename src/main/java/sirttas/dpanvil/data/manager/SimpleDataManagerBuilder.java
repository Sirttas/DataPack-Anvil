package sirttas.dpanvil.data.manager;

import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;

public class SimpleDataManagerBuilder<T> extends AbstractBuilder<T> {

	public SimpleDataManagerBuilder(Class<T> type, @Nonnull ResourceKey<IDataManager<T>> key) {
		super(type, key);
	}
	
	@Override
	public IDataManager<T> build() {
		return new SimpleDataManager<>(key, type, folder, defaultValueFactory, idSetter);
	}
}
