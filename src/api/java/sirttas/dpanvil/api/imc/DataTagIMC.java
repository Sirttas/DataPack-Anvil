package sirttas.dpanvil.api.imc;

import java.util.function.Supplier;

import net.minecraftforge.fml.InterModComms;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.tag.DataTagRegistry;

public class DataTagIMC<T> {

	public static final String METHOD = "data_tag";
	
	private final IDataManager<T> manager;
	private final DataTagRegistry<T> registry;
	
	public DataTagIMC(IDataManager<T> manager, DataTagRegistry<T> registry) {
		this.manager = manager;
		this.registry = registry;
	}

	public IDataManager<T> getManager() {
		return manager;
	}

	public DataTagRegistry<T> getRegistry() {
		return registry;
	}
	
	public static <T> void enqueue(Supplier<DataTagIMC<T>> supplier) {
		InterModComms.sendTo(DataPackAnvilApi.MODID, METHOD, supplier);
	}
}
