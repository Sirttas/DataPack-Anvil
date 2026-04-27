package sirttas.dpanvil.registry;

import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
@EventBusSubscriber(modid = DataPackAnvilApi.MODID)
@Deprecated
public class RegistryListener {

    private final List<Consumer<HolderLookup.Provider>> listeners = new ArrayList<>();
    private final Map<DynamicOps<?>, RegistryOps<?>> registryOps = new Reference2ObjectOpenHashMap<>();

    private HolderLookup.Provider provider;

    private static final RegistryListener INSTANCE = new RegistryListener();

    private RegistryListener() {}

    public static RegistryListener getInstance() {
    	return INSTANCE;
    }

    public synchronized void listen(Consumer<HolderLookup.Provider> listener) {
        if (provider != null) {
            listener.accept(provider);
        } else {
            listeners.add(listener);
        }
    }

    public synchronized <T> RegistryOps<@NotNull T> getRegistryOps(DynamicOps<T> ops) {
        if (provider == null) {
            throw new IllegalStateException("Registry not initialized yet!");
        }
        return (RegistryOps<@NotNull T>) registryOps.computeIfAbsent(ops, o -> provider.createSerializationContext(o));
    }

    private synchronized void clear() {
        provider = null;
        registryOps.clear();
    }

    private synchronized void runListeners(HolderLookup.Provider provider) {
        this.provider = new HolderLookup.Provider() {
            @Override
            public @NonNull Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
                return provider.listRegistryKeys();
            }

            @Override
            public <T> @NonNull Optional<? extends HolderLookup.RegistryLookup<T>> lookup(@NonNull ResourceKey<? extends Registry<? extends T>> key) {
                return provider.lookup(key).map(lookup -> {
                            if (lookup instanceof HolderLookup.RegistryLookup.Delegate<T> delegate) {
                                return delegate.parent();
                            }
                            return lookup;
                        });
            }
        };
        registryOps.clear();
        listeners.forEach(l -> l.accept(this.provider));
        listeners.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void addReloadListeners(AddServerReloadListenersEvent event) {
        INSTANCE.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.shouldUpdateStaticData()) {
            INSTANCE.runListeners(event.getLookupProvider());
        }
    }
}
