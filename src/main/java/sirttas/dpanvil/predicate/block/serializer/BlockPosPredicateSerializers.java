package sirttas.dpanvil.predicate.block.serializer;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.ListPredicate;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.registry.RegistryHelper;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockPosPredicateSerializers {

	private BlockPosPredicateSerializers() {
	}

	@SubscribeEvent
	public static void registerBlockPosPredicateSerializers(RegistryEvent.Register<BlockPosPredicateSerializer<?>> event) {
		IForgeRegistry<BlockPosPredicateSerializer<?>> registry = event.getRegistry();

		RegistryHelper.register(registry, new ListPredicate.Serializer<>(OrBlockPredicate::new), OrBlockPredicate.NAME);
		RegistryHelper.register(registry, new ListPredicate.Serializer<>(AndBlockPredicate::new), AndBlockPredicate.NAME);
		RegistryHelper.register(registry, new NotBlockPredicate.Serializer(), NotBlockPredicate.NAME);
		RegistryHelper.register(registry, new MatchBlockPredicate.Serializer(), MatchBlockPredicate.NAME);
		RegistryHelper.register(registry, new MatchBlockTagPredicate.Serializer(), MatchBlockTagPredicate.NAME);
	}

}
