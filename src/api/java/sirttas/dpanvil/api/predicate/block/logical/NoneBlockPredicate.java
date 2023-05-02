package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NoneBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "none";
	private static final NoneBlockPredicate INSTANCE = new NoneBlockPredicate();
	public static final Codec<NoneBlockPredicate> CODEC = Codec.unit(INSTANCE);

	private NoneBlockPredicate() {
	}

	@Override
	public boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
		return false;
	}

	@Override
	public BlockPosPredicateType<NoneBlockPredicate> getType() {
		return BlockPosPredicateType.NONE.get();
	}

	@Override
	public IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		return IBlockPosPredicate.none();
	}
	
	@Override
	public IBlockPosPredicate not() {
		return IBlockPosPredicate.any();
	}
	
	public static IBlockPosPredicate get() {
		return INSTANCE;
	}
}
