package sirttas.dpanvil.data;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataPackAnvilApi.MODID)
public class DataHandler {

	private static boolean tagsReceived = false;
	private static boolean recipesReceived = false;
	private static boolean dpAnvilReloaded = false;

	private static RecipeManager recipeManager = null;
	private static ITagCollectionSupplier tagManager = null;

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRecipesUpdate(RecipesUpdatedEvent event) {
		recipeManager = event.getRecipeManager();
		recipesReceived = true;
		process();
	}

	@SubscribeEvent
	public static void onTagsUpdate(TagsUpdatedEvent.VanillaTagTypes event) {
		tagsReceived = true;
		tagManager = event.getTagManager();
		process();
	}

	public static void onDPAnvilUpdate() {
		dpAnvilReloaded = true;
		process();
	}

	private static void process() {
		if (recipesReceived && tagsReceived && dpAnvilReloaded) {
			DataPackAnvil.ANNOTATION_PROCESSOR.applyDataHolder();
			MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(recipeManager, tagManager, DataPackAnvil.WRAPPER.getDataManagers()));
			reloadJEI();
			recipeManager = null;
			tagManager = null;
			recipesReceived = false;
			tagsReceived = false;
			dpAnvilReloaded = false;
		}
	}

	private static void reloadJEI() {
		DataPackAnvilApi.LOGGER.info("JEI loaded before all data are received, atempting to reload JEI");
		IResourceManager manager = Minecraft.getInstance().getResourceManager();

		try {
			Optional.ofNullable(manager).filter(SimpleReloadableResourceManager.class::isInstance).map(SimpleReloadableResourceManager.class::cast).map(r -> r.reloadListeners.stream())
					.orElse(Stream.empty()).filter(r -> "JeiReloadListener".equals(r.getClass().getSimpleName())).map(ISelectiveResourceReloadListener.class::cast) // NOSONAR
					.forEach(r -> r.onResourceManagerReload(manager, t -> true));
		} catch (Exception e) {
			DataPackAnvilApi.LOGGER.error("Error while reloading JEI", e);
		}
	}
}
