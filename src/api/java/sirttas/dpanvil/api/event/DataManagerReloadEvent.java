package sirttas.dpanvil.api.event;

import net.minecraftforge.eventbus.api.GenericEvent;
import sirttas.dpanvil.api.data.IDataManager;

public class DataManagerReloadEvent<T> extends GenericEvent<T> {

	private final IDataManager<T> dataManager;

	public DataManagerReloadEvent(IDataManager<T> dataManager) {
		super(dataManager.getContentType());
		this.dataManager = dataManager;
	}

	public IDataManager<T> getDataManager() {
		return dataManager;
	}
}
