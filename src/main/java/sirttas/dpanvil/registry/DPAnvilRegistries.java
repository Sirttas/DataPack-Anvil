package sirttas.dpanvil.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DPAnvilRegistries {

	private static final int MIN_ID = 0;
	private static final int MAX_ID = Short.MAX_VALUE - 1;

	private DPAnvilRegistries() {}
	
	@SubscribeEvent
	public static void createRegistries(NewRegistryEvent event) {
		event.create(new RegistryBuilder<>().setName(DataPackAnvil.createRL("block_predicate_type")).setIDRange(MIN_ID, MAX_ID).setType(cast()));
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> cast() {
		return (Class<T>) BlockPosPredicateType.class;
	}
}
