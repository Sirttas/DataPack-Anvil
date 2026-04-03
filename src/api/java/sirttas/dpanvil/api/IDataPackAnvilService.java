package sirttas.dpanvil.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;

public interface IDataPackAnvilService {

    @Nonnull
    <T> IDataManager.Builder<T> createDataManagerBuilder(@Nonnull Class<T> type, @Nonnull ResourceKey<@NotNull IDataManager<T>> key);

    @Nonnull
    <T> Codec<Holder<@NotNull T>> holderCodec(ResourceKey<? super @NotNull IDataManager<T>> managerKey, Codec<T> elementCodec, boolean allowInline);

    @NotNull
    <T> StreamCodec<@NotNull ByteBuf, @NotNull Holder<@NotNull T>> streamCodec(ResourceKey<? super @NotNull IDataManager<T>> managerKey);
}
