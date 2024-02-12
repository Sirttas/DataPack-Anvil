package sirttas.dpanvil.data;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompleteEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataPackAnvilApi.MODID)
public class DataHandler {

	private static RecipeManager recipeManager = null;
	private static RegistryAccess registry = null;
	private static final Map<Class<? extends Event>, Boolean> map = new HashMap<>();
	
	static {
		map.put(RecipesUpdatedEvent.class, false);
		map.put(TagsUpdatedEvent.class, false);
		map.put(DataPackReloadCompleteEvent.class, false);
	}

	private DataHandler() {}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		registry = event.getRegistryAccess();
		process(event.getClass());
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
			NeoForge.EVENT_BUS.post(new DataPackReloadCompleteEvent(recipeManager, DataPackAnvil.WRAPPER.getDataManagers(), registry));
			recipeManager = null;
			map.replaceAll((k, v) -> false);
		}
	}
}
