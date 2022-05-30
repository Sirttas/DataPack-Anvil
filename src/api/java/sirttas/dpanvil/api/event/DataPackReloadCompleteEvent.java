package sirttas.dpanvil.api.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;
import sirttas.dpanvil.api.data.IDataManager;

import java.util.Map;

public class DataPackReloadCompleteEvent extends Event {

	private final RecipeManager recipeManager;
	private final Map<ResourceKey<IDataManager<?>>, IDataManager<?>> dataManagers;

	public DataPackReloadCompleteEvent(RecipeManager recipeManager, Map<ResourceKey<IDataManager<?>>, IDataManager<?>> dataManagers) {
		this.recipeManager = recipeManager;
		this.dataManagers = dataManagers;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public Map<ResourceKey<IDataManager<?>>, IDataManager<?>> getDataManagers() {
		return dataManagers;
	}
}
