package sirttas.dpanvil.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;
import sirttas.dpanvil.api.data.IDataManager;

import java.util.Map;

public class DataPackReloadCompletEvent extends Event {

	private final RecipeManager recipeManager;
	private final Map<ResourceLocation, IDataManager<?>> dataManagers;

	public DataPackReloadCompletEvent(RecipeManager recipeManager, Map<ResourceLocation, IDataManager<?>> dataManagers) {
		this.recipeManager = recipeManager;
		this.dataManagers = dataManagers;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public Map<ResourceLocation, IDataManager<?>> getDataManagers() {
		return dataManagers;
	}
}
