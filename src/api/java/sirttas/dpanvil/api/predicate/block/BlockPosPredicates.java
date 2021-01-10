package sirttas.dpanvil.api.predicate.block;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag.INamedTag;
import sirttas.dpanvil.api.codec.CodecHelper;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.AnyBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NoneBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockStatePredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;

public class BlockPosPredicates {

	public static final Codec<IBlockPosPredicate> CODEC = CodecHelper.getRegistryCodec(() -> BlockPosPredicateType.REGISTRY).dispatch(IBlockPosPredicate::getType, BlockPosPredicateType::getCodec);

	public static IBlockPosPredicate any() {
		return AnyBlockPredicate.INSTANCE;
	}

	public static IBlockPosPredicate none() {
		return NoneBlockPredicate.INSTANCE;
	}

	public static IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		return new OrBlockPredicate(predicates);
	}

	public static IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		return new AndBlockPredicate(predicates);
	}

	public static IBlockPosPredicate not(IBlockPosPredicate predicates) {
		return new NotBlockPredicate(predicates);
	}

	public static IBlockPosPredicate match(Block... blocks) {
		if (blocks.length == 1) {
			return new MatchBlockPredicate(blocks[0]);
		}
		return new MatchBlocksPredicate(blocks);
	}

	public static IBlockPosPredicate match(INamedTag<Block> tag) {
		return new MatchBlockTagPredicate(tag);
	}

	public static IBlockPosPredicate match(BlockState state) {
		return new MatchBlockStatePredicate(state);
	}
}
