package sirttas.dpanvil.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;

public interface IDataPackAnvilService {

    @Nonnull
    <T> IDataManager.Builder<T> createDataManagerBuilder(@Nonnull Class<T> type, @Nonnull ResourceKey<@NotNull IDataManager<T>> key);

    @Nonnull
    <E> Codec<Holder<@NotNull E>> holderCodec(ResourceKey<? extends @NotNull IDataManager<E>> managerKey, Codec<E> elementCodec, boolean allowInline);

}
