package sirttas.dpanvil;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.IDataPackAnvilService;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.manager.DataFileCodec;
import sirttas.dpanvil.data.manager.DataManagerBuilder;

import javax.annotation.Nonnull;

public class DataPackAnvilService implements IDataPackAnvilService {
    @Nonnull
    @Override
    public <T> IDataManager.Builder<T> createDataManagerBuilder(@Nonnull Class<T> type, @Nonnull ResourceKey<@NotNull IDataManager<T>> key) {
        return new DataManagerBuilder<>(type, key);
    }

    @NotNull
    @Override
    public <E> Codec<Holder<E>> holderCodec(ResourceKey<? extends @NotNull IDataManager<E>> managerKey, Codec<E> elementCodec, boolean allowInline) {
        return new DataFileCodec<>(managerKey, elementCodec, allowInline);
    }
}
