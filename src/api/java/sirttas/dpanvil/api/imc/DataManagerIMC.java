package sirttas.dpanvil.api.imc;

import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.DataManager;

public class DataManagerIMC<T> {

	public static final String METHOD = "data_manager";

	private final ResourceLocation id;
	private final DataManager<T> manager;

	public DataManagerIMC(ResourceLocation id, DataManager<T> manager) {
		this.id = id;
		this.manager = manager;
	}

	public ResourceLocation getId() {
		return id;
	}

	public DataManager<T> getManager() {
		return manager;
	}

}
