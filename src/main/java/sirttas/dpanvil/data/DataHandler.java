package sirttas.dpanvil.data;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
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
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;

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

	@SubscribeEvent(priority = EventPriority.HIGH)
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

	private static void process(Class<? extends Event> clazz) {
		map.put(clazz, true);
		if (map.values().stream().allMatch(b -> b)) {
			DataPackAnvil.ANNOTATION_PROCESSOR.applyDataHolder();
			MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(recipeManager, tagManager, DataPackAnvil.WRAPPER.getDataManagers()));
			reloadJEI();
			recipeManager = null;
			tagManager = null;
			map.keySet().forEach(k -> map.put(k, false));
		}
	}

	private static void reloadJEI() {
		DataPackAnvilApi.LOGGER.info("JEI loaded before all data are received, atempting to reload JEI");
		IResourceManager manager = Minecraft.getInstance().getResourceManager();

		try {
			Optional.ofNullable(manager).filter(SimpleReloadableResourceManager.class::isInstance).map(SimpleReloadableResourceManager.class::cast).map(r -> r.reloadListeners.stream())
					.orElse(Stream.empty()).filter(r -> "JeiReloadListener".equals(r.getClass().getSimpleName())).map(ISelectiveResourceReloadListener.class::cast)
					.forEach(r -> r.onResourceManagerReload(manager, t -> true));
		} catch (Exception e) {
			DataPackAnvilApi.LOGGER.error("Error while reloading JEI", e);
		}
	}
}
