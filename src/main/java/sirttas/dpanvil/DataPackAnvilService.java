package sirttas.dpanvil;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.IDataPackAnvilService;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.manager.DataFileCodec;
import sirttas.dpanvil.data.manager.DataFileStreamCodec;
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
    public <T> Codec<Holder<@NotNull T>> holderCodec(ResourceKey<? super @NotNull IDataManager<T>> managerKey, Codec<T> elementCodec, boolean allowInline) {
        return new DataFileCodec<>(managerKey, elementCodec, allowInline);
    }

    @NotNull
    @Override
    public <T> StreamCodec<@NotNull ByteBuf, @NotNull Holder<@NotNull T>> streamCodec(ResourceKey<? super @NotNull IDataManager<T>> managerKey) {
        return new DataFileStreamCodec<>(managerKey);
    }
}
