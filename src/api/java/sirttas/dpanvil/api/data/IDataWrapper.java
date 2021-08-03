package sirttas.dpanvil.api.data;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.resources.ResourceLocation;

/**
 * A wrapper of an object present in a {@link IDataManager}
 *
 * @param <T>
 */
public interface IDataWrapper<T> extends Supplier<T> {

	public boolean isPresent();
	public ResourceLocation getId();
	
    public default Stream<T> stream() {
        return isPresent() ? Stream.of(get()) : Stream.of();
    }

    public default void ifPresent(Consumer<? super T> consumer) {
        if (isPresent())
            consumer.accept(get());
    }
    
	public static <T> Codec<IDataWrapper<T>> codec(IDataManager<T> manager) {
		return new Codec<>() {
			@Override
			public <U> DataResult<Pair<IDataWrapper<T>, U>> decode(final DynamicOps<U> ops, final U input) {
				return ResourceLocation.CODEC.decode(ops, input).map(pair -> pair.mapFirst(manager::getWrapper));
			}

			@Override
			public <U> DataResult<U> encode(final IDataWrapper<T> input, final DynamicOps<U> ops, final U prefix) {
				return ResourceLocation.CODEC.encode(input.getId(), ops, prefix);
			}
		};
	}
}
