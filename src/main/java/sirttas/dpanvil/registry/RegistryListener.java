package sirttas.dpanvil.registry;

import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID)
public class RegistryListener {

    private final List<Consumer<RegistryAccess>> listeners = new ArrayList<>();
    private final Map<DynamicOps<?>, RegistryOps<?>> registryOps = new Reference2ObjectOpenHashMap<>();

    private RegistryAccess registry;

    private static final RegistryListener INSTANCE = new RegistryListener();

    private RegistryListener() {}

    public static RegistryListener getInstance() {
    	return INSTANCE;
    }

    public synchronized void listen(Consumer<RegistryAccess> listener) {
        if (registry != null) {
            listener.accept(registry);
        } else {
            listeners.add(listener);
        }
    }

    public synchronized <T> RegistryOps<T> getRegistryOps(DynamicOps<T> ops) {
        if (registry == null) {
            throw new IllegalStateException("Registry not initialized yet!");
        }
        return (RegistryOps<T>) registryOps.computeIfAbsent(ops, o -> RegistryOps.create(o, registry));
    }

    private synchronized void clear() {
        registry = null;
        registryOps.clear();
    }

    private synchronized void runListeners(RegistryAccess registryAccess) {
        registry = registryAccess;
        registryOps.clear();
        listeners.forEach(l -> l.accept(registry));
        listeners.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void addReloadListeners(AddReloadListenerEvent event) {
        INSTANCE.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        INSTANCE.runListeners(event.getRegistryAccess().freeze());
    }
}
