package sirttas.dpanvil.api.event;

import java.util.Map;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.tags.TagContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import sirttas.dpanvil.api.data.IDataManager;

public class DataPackReloadCompletEvent extends Event {

	private final RecipeManager recipeManager;
	private final TagContainer tagManager;
	private final Map<ResourceLocation, IDataManager<?>> dataMnagers;

	public DataPackReloadCompletEvent(RecipeManager recipeManager, TagContainer tagManager, Map<ResourceLocation, IDataManager<?>> dataMnagers) {
		this.recipeManager = recipeManager;
		this.tagManager = tagManager;
		this.dataMnagers = dataMnagers;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public TagContainer getTagManager() {
		return tagManager;
	}

	public Map<ResourceLocation, IDataManager<?>> getDataManagers() {
		return dataMnagers;
	}
}
