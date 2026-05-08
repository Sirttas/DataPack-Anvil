package sirttas.dpanvil.api.event;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.data.IDataManager;

@Deprecated
public class DataManagerReloadEvent extends Event {

	private final IDataManager<?> dataManager;

	public DataManagerReloadEvent(IDataManager<?> dataManager) {
		this.dataManager = dataManager;
	}

	@SuppressWarnings("unchecked")
    public <T> IDataManager<T> getDataManager() {
		return (IDataManager<T>) dataManager;
	}

	public <T> boolean isFor(ResourceKey<@NotNull IDataManager<T>> key) {
		return dataManager.getKey().equals(key);
	}
}
