package sirttas.dpanvil.registry;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryBuilder;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DPAnvilRegistries {

	private static final int MIN_ID = 0;
	private static final int MAX_ID = Short.MAX_VALUE - 1;

	@SubscribeEvent
	public static void createRegistries(RegistryEvent.NewRegistry event) {
		new RegistryBuilder<>().setName(DataPackAnvil.createRL("block_predicate_serializer")).setIDRange(MIN_ID, MAX_ID).setType(cast(BlockPosPredicateSerializer.class)).create();
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> cast(Class<?> cls) {
		return (Class<T>) cls;
	}
}
