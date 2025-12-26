package sirttas.dpanvil.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Supplier;

public class LazyCodec<T> implements Codec<T> {

    private final Lazy<Codec<T>> codec;

    private LazyCodec(Lazy<Codec<T>> codec) {
        this.codec = codec;
    }

    public static <T> LazyCodec<T> of(Lazy<Codec<T>> codec) {
        return new LazyCodec<>(codec);
    }

    public static <T> LazyCodec<T> of(Supplier<Codec<T>> codec) {
        return LazyCodec.of(Lazy.of(codec));
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return codec.get().decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return codec.get().encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "LazyCodec[" + codec.get() + "]";
    }
}
