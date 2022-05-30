package sirttas.dpanvil.api;

import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;

public interface IDataPackAnvilService {

    @Nonnull
    <T> IDataManager.Builder<T> createDataManagerBuilder(@Nonnull Class<T> type, @Nonnull String folder);

}
