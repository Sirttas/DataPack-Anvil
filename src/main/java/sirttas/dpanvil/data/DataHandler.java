package sirttas.dpanvil.data;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent.CustomTagTypes;
import net.minecraftforge.event.TagsUpdatedEvent.VanillaTagTypes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;
import sirttas.dpanvil.interaction.jei.JeiLoadDelayer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataPackAnvilApi.MODID)
public class DataHandler {

	private static RecipeManager recipeManager = null;
	private static ITagCollectionSupplier tagManager = null;
	private static Map<Class<? extends Event>, Boolean> map = Maps.newHashMap();
	
	static {
		map.put(RecipesUpdatedEvent.class, false);
		map.put(VanillaTagTypes.class, false);
		map.put(CustomTagTypes.class, false);
		map.put(DataPackReloadCompletEvent.class, false);
	}

	private DataHandler() {}
	
	public static void addEvent(Class<? extends Event> eventType) {
		map.put(eventType, false);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventType, e -> process(eventType));
	}
	
	@SubscribeEvent
	public static void onRecipesUpdate(RecipesUpdatedEvent event) {
		recipeManager = event.getRecipeManager();
		process(event.getClass());
	}

	@SubscribeEvent
	public static void onVanillaTagsUpdate(VanillaTagTypes event) {
		tagManager = event.getTagManager();
		process(event.getClass());
	}
	
	@SubscribeEvent
	public static void onCustomTagsUpdate(CustomTagTypes event) {
		tagManager = event.getTagManager();
		process(event.getClass());
	}

	public static void onDPAnvilUpdate() {
		process(DataPackReloadCompletEvent.class);
	}

	private static void process(Class<? extends Event> eventType) {
		map.put(eventType, true);
		if (map.values().stream().allMatch(b -> b)) {
			DataPackAnvil.ANNOTATION_PROCESSOR.applyDataHolder();
			MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(recipeManager, tagManager, DataPackAnvil.WRAPPER.getDataManagers()));
			JeiLoadDelayer.loadJEI();
			recipeManager = null;
			tagManager = null;
			map.replaceAll((k, v) -> false);
		}
	}
}
