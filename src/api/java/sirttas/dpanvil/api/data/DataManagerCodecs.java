package sirttas.dpanvil.api.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.DataPackAnvilApi;

public class DataManagerCodecs {

    private DataManagerCodecs() {}

    public static <E> Codec<Holder<@NotNull E>> holderCodec(ResourceKey<? super @NotNull IDataManager<E>> managerKey, Codec<E> elementCodec) {
        return holderCodec(managerKey, elementCodec, true);
    }

    public static <E> Codec<Holder<@NotNull E>> holderCodec(ResourceKey<? super @NotNull IDataManager<E>> managerKey, Codec<E> elementCodec, boolean allowInline) {
        return DataPackAnvilApi.service().holderCodec(managerKey, elementCodec, allowInline);
    }

    public static @NotNull <E> StreamCodec<@NotNull ByteBuf, @NotNull Holder<@NotNull E>> streamCodec(ResourceKey<? super @NotNull IDataManager<E>> managerKey) {
        return DataPackAnvilApi.service().streamCodec(managerKey);
    }
}
