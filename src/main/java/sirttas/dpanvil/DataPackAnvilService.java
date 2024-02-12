package sirttas.dpanvil;

import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.IDataPackAnvilService;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.manager.SimpleDataManagerBuilder;

import javax.annotation.Nonnull;

public class DataPackAnvilService implements IDataPackAnvilService {
    @Nonnull
    @Override
    public <T> IDataManager.Builder<T> createDataManagerBuilder(@Nonnull Class<T> type, @Nonnull ResourceKey<IDataManager<T>> key) {
        return new SimpleDataManagerBuilder<>(type, key);
    }
}
