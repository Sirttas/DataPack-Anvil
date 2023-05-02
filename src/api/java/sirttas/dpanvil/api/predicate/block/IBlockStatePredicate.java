package sirttas.dpanvil.api.predicate.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface IBlockStatePredicate extends IBlockPosPredicate {

	boolean test(BlockState state);

	default Predicate<BlockState> asBlockStatePredicate() {
		return this::test;
	}

	@Override
	default boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
		return test(level.getBlockState(pos));
	}
}
