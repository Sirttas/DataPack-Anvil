package sirttas.dpanvil.api.imc;

import net.minecraftforge.fml.InterModComms;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.tag.DataTagRegistry;

import java.util.function.Supplier;

public record DataTagIMC<T>(
		IDataManager<T> manager,
		DataTagRegistry<T> registry
) {

	public static final String METHOD = "data_tag";

	public static <T> void enqueue(Supplier<DataTagIMC<T>> supplier) {
		InterModComms.sendTo(DataPackAnvilApi.MODID, METHOD, supplier);
	}
}
