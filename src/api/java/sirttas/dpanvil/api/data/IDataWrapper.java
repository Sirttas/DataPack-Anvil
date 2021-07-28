package sirttas.dpanvil.api.data;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
}
