package sirttas.dpanvil.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;
import sirttas.dpanvil.api.data.IDataManager;

import java.util.Map;

public class DataPackReloadCompletEvent extends Event {

	private final RecipeManager recipeManager;
	private final TagContainer tagManager;
	private final Map<ResourceLocation, IDataManager<?>> dataManagers;

	public DataPackReloadCompletEvent(RecipeManager recipeManager, TagContainer tagManager, Map<ResourceLocation, IDataManager<?>> dataManagers) {
		this.recipeManager = recipeManager;
		this.tagManager = tagManager;
		this.dataManagers = dataManagers;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public TagContainer getTagManager() {
		return tagManager;
	}

	public Map<ResourceLocation, IDataManager<?>> getDataManagers() {
		return dataManagers;
	}
}
