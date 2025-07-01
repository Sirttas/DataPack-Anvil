package sirttas.dpanvil.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.DataPackAnvilApi;

public class DataManagerCodecs {

    private DataManagerCodecs() {}

    public static <E> Codec<Holder<E>> holderCodec(ResourceKey<? extends IDataManager<E>> managerKey, Codec<E> elementCodec) {
        return holderCodec(managerKey, elementCodec, true);
    }

    public static <E> Codec<Holder<E>> holderCodec(ResourceKey<? extends IDataManager<E>> managerKey, Codec<E> elementCodec, boolean allowInline) {
        return DataPackAnvilApi.service().holderCodec(managerKey, elementCodec, allowInline);
    }
}
