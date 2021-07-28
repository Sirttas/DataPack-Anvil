package sirttas.dpanvil.api.predicate.block;

import java.util.function.Predicate;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public interface IBlockStatePredicate extends IBlockPosPredicate {

	boolean test(BlockState state);

	default Predicate<BlockState> asBlockStatePredicate() {
		return this::test;
	}

	@Override
	default boolean test(LevelReader world, BlockPos pos) {
		return test(world.getBlockState(pos));
	}
}
