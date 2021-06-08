package sirttas.dpanvil.api.predicate.block;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface IBlockStatePredicate extends IBlockPosPredicate {

	boolean test(BlockState state);

	default Predicate<BlockState> asBlockStatePredicate() {
		return this::test;
	}

	@Override
	default boolean test(IWorldReader world, BlockPos pos) {
		return test(world.getBlockState(pos));
	}
}
