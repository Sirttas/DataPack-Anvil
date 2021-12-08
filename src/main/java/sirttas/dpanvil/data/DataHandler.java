package sirttas.dpanvil.data;

import net.minecraft.tags.TagContainer;
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
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataPackAnvilApi.MODID)
public class DataHandler {

	private static RecipeManager recipeManager = null;
	private static TagContainer tagManager = null;
	private static final Map<Class<? extends Event>, Boolean> map = new HashMap<>();
	
	static {
		map.put(RecipesUpdatedEvent.class, false);
		map.put(TagsUpdatedEvent.class, false);
		map.put(DataPackReloadCompletEvent.class, false);
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

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onVanillaTagsUpdate(TagsUpdatedEvent event) {
		tagManager = event.getTagManager();
		process(event.getClass());
	}

	public static void onDPAnvilUpdate() {
		process(DataPackReloadCompletEvent.class);
	}

	private static void process(Class<? extends Event> eventType) {
		map.put(eventType, true);
		if (map.values().stream().allMatch(b -> b)) {
			MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(recipeManager, tagManager, DataPackAnvil.WRAPPER.getDataManagers()));
			recipeManager = null;
			tagManager = null;
			map.replaceAll((k, v) -> false);
		}
	}
}
