package sirttas.dpanvil.predicate.block.serializer;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;
import sirttas.dpanvil.predicate.block.serializer.logical.ListBlockPredicateSerializer;
import sirttas.dpanvil.predicate.block.serializer.logical.NotBlockPredicateSerializer;
import sirttas.dpanvil.predicate.block.serializer.match.MatchBlockPredicateSerializer;
import sirttas.dpanvil.predicate.block.serializer.match.MatchBlockTagPredicateSerializer;
import sirttas.dpanvil.predicate.block.serializer.match.MatchBlocksPredicateSerializer;
import sirttas.dpanvil.registry.RegistryHelper;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockPosPredicateSerializers {

	private BlockPosPredicateSerializers() {
	}

	@SubscribeEvent
	public static void registerBlockPosPredicateSerializers(RegistryEvent.Register<BlockPosPredicateSerializer<?>> event) {
		IForgeRegistry<BlockPosPredicateSerializer<?>> registry = event.getRegistry();

		RegistryHelper.register(registry, new ListBlockPredicateSerializer<>(OrBlockPredicate::new), OrBlockPredicate.NAME);
		RegistryHelper.register(registry, new ListBlockPredicateSerializer<>(AndBlockPredicate::new), AndBlockPredicate.NAME);
		RegistryHelper.register(registry, new NotBlockPredicateSerializer(), NotBlockPredicate.NAME);
		RegistryHelper.register(registry, new MatchBlockPredicateSerializer(), MatchBlockPredicate.NAME);
		RegistryHelper.register(registry, new MatchBlocksPredicateSerializer(), MatchBlocksPredicate.NAME);
		RegistryHelper.register(registry, new MatchBlockTagPredicateSerializer(), MatchBlockTagPredicate.NAME);
	}

}
