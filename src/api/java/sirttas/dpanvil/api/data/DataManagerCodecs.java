package sirttas.dpanvil.api.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.DataPackAnvilApi;

public class DataManagerCodecs {

    private DataManagerCodecs() {}

    public static <E> Codec<Holder<E>> holderCodec(ResourceKey<? super IDataManager<E>> managerKey, Codec<E> elementCodec) {
        return holderCodec(managerKey, elementCodec, true);
    }

    public static <E> Codec<Holder<E>> holderCodec(ResourceKey<? super IDataManager<E>> managerKey, Codec<E> elementCodec, boolean allowInline) {
        return DataPackAnvilApi.service().holderCodec(managerKey, elementCodec, allowInline);
    }

    public static <E> StreamCodec<ByteBuf, Holder<E>> streamCodec(ResourceKey<? super IDataManager<E>> managerKey) {
        return DataPackAnvilApi.service().streamCodec(managerKey);
    }
}
