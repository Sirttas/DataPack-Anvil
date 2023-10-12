package sirttas.dpanvil.registry;

import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

    public void listen(Consumer<RegistryAccess> listener) {
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void addReloadListeners(AddReloadListenerEvent event) {
        INSTANCE.registry = null;
        INSTANCE.registryOps.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        INSTANCE.registry = event.getRegistryAccess().freeze();
        INSTANCE.registryOps.clear();
        INSTANCE.listeners.forEach(l -> l.accept(INSTANCE.registry));
        INSTANCE.listeners.clear();
    }
}
