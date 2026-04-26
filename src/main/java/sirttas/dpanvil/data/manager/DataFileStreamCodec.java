package sirttas.dpanvil.data.manager;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.data.IDataManager;

public class DataFileStreamCodec<T> implements StreamCodec<@NotNull ByteBuf, @NotNull Holder<@NotNull T>> {

    private final ResourceKey<? super @NotNull IDataManager<T>> managerKey;
    private StreamCodec<@NotNull ByteBuf, @NotNull Holder<@NotNull T>> delegate;

    public DataFileStreamCodec(ResourceKey<? super @NotNull IDataManager<T>> managerKey) {
        this.managerKey = managerKey;
    }

    private StreamCodec<@NotNull ByteBuf, @NotNull Holder<@NotNull T>> getOrCreateDelegate() {
        if (this.delegate == null) {
            DataManager<T> manager = DataPackAnvil.WRAPPER.getManager(managerKey);

            if (manager == null) {
                throw new IllegalStateException("Could not find manager with key " + managerKey);
            }
            delegate = Identifier.STREAM_CODEC.map(manager::getOrCreateHolder, h -> h.getKey().identifier());
        }
        return this.delegate;
    }


    @Override
    public @NotNull Holder<@NotNull T> decode(@NotNull ByteBuf input) {
        return getOrCreateDelegate().decode(input);
    }

    @Override
    public void encode(@NotNull ByteBuf output, @NotNull Holder<@NotNull T> value) {
        getOrCreateDelegate().encode(output, value);
    }
}
