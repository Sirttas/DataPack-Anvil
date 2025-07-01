package sirttas.dpanvil.data.manager;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.data.IDataManager;

public class DataFileCodec<E> implements Codec<Holder<E>> {
    private final ResourceKey<IDataManager<E>> managerKey;
    private final Codec<E> elementCodec;
    private final boolean allowInline;

    @SuppressWarnings("unchecked")
    public DataFileCodec(ResourceKey<? extends IDataManager<E>> managerKey, Codec<E> elementCodec, boolean allowInline) {
        this.managerKey = (ResourceKey<IDataManager<E>>) managerKey;
        this.elementCodec = elementCodec;
        this.allowInline = allowInline;
    }

    public <T> DataResult<T> encode(Holder<E> holder, DynamicOps<T> ops, T prefix) {
        return holder.unwrap().map(
                k -> ResourceLocation.CODEC.encode(k.location(), ops, prefix),
                e -> this.elementCodec.encode(e, ops, prefix)
        );
    }

    @Override
    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> ops, T input) {
        var manager = DataPackAnvil.WRAPPER.getManager(this.managerKey);

        if (manager == null) {
            return DataResult.error(() -> "DataManager does not exist: " + this.managerKey);
        }

        var opt = ResourceLocation.CODEC.decode(ops, input).result();

        if (opt.isEmpty()) {
            return !this.allowInline
                    ? DataResult.error(() -> "Inline definitions not allowed here")
                    : this.elementCodec.decode(ops, input).map(p -> p.mapFirst(Holder::direct));
        }

        var pair = opt.get();
        var resourcekey = IDataManager.createKey(this.managerKey, pair.getFirst());
        var result = DataResult.success(manager.getOrCreateHolder(resourcekey));

        return result.map(v -> Pair.of(v, pair.getSecond())).setLifecycle(Lifecycle.stable());
    }

    @Override
    public String toString() {
        return "DataFileCodec[" + this.managerKey + " " + this.elementCodec + "]";
    }
}
