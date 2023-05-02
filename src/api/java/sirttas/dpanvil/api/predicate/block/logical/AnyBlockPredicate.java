package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AnyBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "any";
	private static final AnyBlockPredicate INSTANCE = new AnyBlockPredicate();
	public static final Codec<AnyBlockPredicate> CODEC = Codec.unit(INSTANCE);

	private AnyBlockPredicate() {}

	@Override
	public boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	@Override
	public BlockPosPredicateType<AnyBlockPredicate> getType() {
		return BlockPosPredicateType.ANY.get();
	}

	@Override
	public IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		return IBlockPosPredicate.any();
	}
	
	@Override
	public IBlockPosPredicate not() {
		return IBlockPosPredicate.none();
	}

	public static IBlockPosPredicate get() {
		return INSTANCE;
	}
}
