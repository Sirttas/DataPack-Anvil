package sirttas.dpanvil.data;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompleteEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataPackAnvilApi.MODID)
public class DataHandler {

	private static RecipeManager recipeManager = null;
	private static final Map<Class<? extends Event>, Boolean> map = new HashMap<>();
	
	static {
		map.put(RecipesUpdatedEvent.class, false);
		map.put(TagsUpdatedEvent.class, false);
		map.put(DataPackReloadCompleteEvent.class, false);
	}

	private DataHandler() {}
	
	public static void addEvent(Class<? extends Event> eventType) {
		map.put(eventType, false);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventType, e -> process(eventType));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRecipesUpdate(RecipesUpdatedEvent event) {
		recipeManager = event.getRecipeManager();
		process(event.getClass());
	}

	public static void onDPAnvilUpdate() {
		process(DataPackReloadCompleteEvent.class);
	}

	private static void process(Class<? extends Event> eventType) {
		map.put(eventType, true);
		if (map.values().stream().allMatch(b -> b)) {
			MinecraftForge.EVENT_BUS.post(new DataPackReloadCompleteEvent(recipeManager, DataPackAnvil.WRAPPER.getDataManagers()));
			recipeManager = null;
			map.replaceAll((k, v) -> false);
		}
	}
}
