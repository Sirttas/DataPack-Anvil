package sirttas.dpanvil.predicate.block;

import com.mojang.serialization.Codec;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;
import sirttas.dpanvil.registry.RegistryHelper;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockPosPredicateTypes {

	private BlockPosPredicateTypes() {
	}

	@SubscribeEvent
	public static void registerBlockPosPredicateSerializers(RegistryEvent.Register<BlockPosPredicateType<?>> event) {
		IForgeRegistry<BlockPosPredicateType<?>> registry = event.getRegistry();

		register(registry, OrBlockPredicate.CODEC, OrBlockPredicate.NAME);
		register(registry, AndBlockPredicate.CODEC, AndBlockPredicate.NAME);
		register(registry, NotBlockPredicate.CODEC, NotBlockPredicate.NAME);
		register(registry, MatchBlockPredicate.CODEC, MatchBlockPredicate.NAME);
		register(registry, MatchBlocksPredicate.CODEC, MatchBlocksPredicate.NAME);
		register(registry, MatchBlockTagPredicate.CODEC, MatchBlockTagPredicate.NAME);
	}

	private static <T extends IBlockPosPredicate> void register(IForgeRegistry<BlockPosPredicateType<?>> registry, Codec<T> codec, String name) {
		RegistryHelper.register(registry, new BlockPosPredicateType<>(codec), name);
	}


}
