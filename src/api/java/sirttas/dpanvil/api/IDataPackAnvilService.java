package sirttas.dpanvil.api;

import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;

public interface IDataPackAnvilService {

    @Nonnull
    <T> IDataManager.Builder<T> createDataManagerBuilder(@Nonnull Class<T> type, @Nonnull ResourceKey<IDataManager<T>> key);
}
