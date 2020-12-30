package sirttas.dpanvil.api.predicate.block;

import net.minecraft.block.Block;
import net.minecraft.tags.ITag.INamedTag;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;

public class BlockPredicates {

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
}
