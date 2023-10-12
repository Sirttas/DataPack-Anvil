package sirttas.dpanvil.api.data.remap;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.AbstractManagedDataBuilderProvider;
import sirttas.dpanvil.api.data.IDataManager;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractRemapKeysProvider extends AbstractManagedDataBuilderProvider<RemapKeys, RemapKeys.Builder> {

    protected AbstractRemapKeysProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, DataPackAnvilApi.REMAP_KEYS_MANAGER, RemapKeys.Builder.ENCODER);
    }

    protected RemapKeys.Builder remap(ResourceKey<? extends IDataManager<?>> key) {
        var builder = RemapKeys.builder();

        add(key.location(), builder);
        return builder;
    }
}
