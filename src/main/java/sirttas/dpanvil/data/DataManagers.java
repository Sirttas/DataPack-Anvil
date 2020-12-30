package sirttas.dpanvil.data;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.DataManager;
import sirttas.dpanvil.api.imc.DataManagerIMC;

public class DataManagers {

	static final Map<ResourceLocation, DataManager<?>> MANAGERS = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	public static <T, M extends DataManager<T>> M getManager(ResourceLocation id) {
		return (M) MANAGERS.get(id);
	}

	public static <T> void putManagerFromIMC(Supplier<DataManagerIMC<T>> imc) {
		DataManagerIMC<T> message = imc.get();

		putManager(message.getId(), message.getManager());
	}

	public static <T, M extends DataManager<T>> void putManager(ResourceLocation id, M manager) {
		MANAGERS.put(id, manager);
	}

	public static Collection<DataManager<?>> all() {
		return MANAGERS.values();
	}

}
