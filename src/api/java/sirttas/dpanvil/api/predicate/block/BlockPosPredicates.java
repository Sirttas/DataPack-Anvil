package sirttas.dpanvil.api.predicate.block;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag.INamedTag;

@Deprecated
public class BlockPosPredicates {

	public static final Codec<IBlockPosPredicate> CODEC = IBlockPosPredicate.CODEC;

	private BlockPosPredicates() {}
	
	public static IBlockPosPredicate any() {
		return IBlockPosPredicate.any();
	}

	public static IBlockPosPredicate none() {
		return IBlockPosPredicate.none();
	}

	public static IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		return IBlockPosPredicate.createOr(predicates);
	}

	public static IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		return IBlockPosPredicate.createAnd(predicates);
	}

	public static IBlockPosPredicate not(IBlockPosPredicate predicate) {
		return predicate.not();
	}

	public static IBlockPosPredicate match(Block... blocks) {
		return IBlockPosPredicate.match(blocks);
	}

	public static IBlockPosPredicate match(INamedTag<Block> tag) {
		return IBlockPosPredicate.match(tag);
	}

	public static IBlockPosPredicate match(BlockState state) {
		return IBlockPosPredicate.match(state);
	}

}
