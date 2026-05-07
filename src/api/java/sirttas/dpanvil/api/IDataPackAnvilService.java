package sirttas.dpanvil.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.data.IDataManager;

public interface IDataPackAnvilService {

    <T> IDataManager.Builder<T> createDataManagerBuilder(Class<T> type, ResourceKey<IDataManager<T>> key);

    <T> Codec<Holder<T>> holderCodec(ResourceKey<? super IDataManager<T>> managerKey, Codec<T> elementCodec, boolean allowInline);

    <T> StreamCodec<ByteBuf, Holder<T>> streamCodec(ResourceKey<? super IDataManager<T>> managerKey);
}
