package sirttas.dpanvil.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import sirttas.dpanvil.DataPackAnvil;

public class RegistryHelper {
	
	private RegistryHelper() {}
	
	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing,
			ResourceLocation name) {
		reg.register(thing.setRegistryName(name));
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, IForgeRegistryEntry<V> thing,
			String name) {
		register(reg, thing, DataPackAnvil.createRL(name));
	}
}
