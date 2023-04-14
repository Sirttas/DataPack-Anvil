package sirttas.dpanvil.api.data.remap;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.AbstractManagedDataBuilderProvider;
import sirttas.dpanvil.api.data.IDataManager;

public abstract class AbstractRemapKeysProvider extends AbstractManagedDataBuilderProvider<RemapKeys, RemapKeys.Builder> {

    protected AbstractRemapKeysProvider(DataGenerator generator) {
        super(generator, DataPackAnvilApi.REMAP_KEYS_MANAGER, RemapKeys.Builder.ENCODER);
    }

    protected RemapKeys.Builder remap(ResourceKey<? extends IDataManager<?>> key) {
        var builder = RemapKeys.builder();

        add(key.location(), builder);
        return builder;
    }
}
