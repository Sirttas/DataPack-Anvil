package sirttas.dpanvil.data;

import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID)
public class TagListener {

    private static final List<Runnable> LISTENERS = new ArrayList<>();

    private TagListener() {}

    public static void listen(Runnable listener) {
        LISTENERS.add(listener);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        LISTENERS.forEach(Runnable::run);
        LISTENERS.clear();
    }
}
