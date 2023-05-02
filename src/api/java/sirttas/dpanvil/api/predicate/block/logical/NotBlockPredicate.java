package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record NotBlockPredicate(
		IBlockPosPredicate predicate
) implements IBlockPosPredicate {

	public static final String NAME = "not";
	public static final Codec<NotBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(NotBlockPredicate::predicate)
	).apply(builder, NotBlockPredicate::new));

	@Override
	public boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
		return !predicate.test(level, pos, direction);
	}

	@Override
	public BlockPosPredicateType<NotBlockPredicate> getType() {
		return BlockPosPredicateType.NOT.get();
	}

	@Override
	public IBlockPosPredicate not() {
		return predicate;
	}

	@Override
	public IBlockPosPredicate simplify() {
		IBlockPosPredicate simplified = this.predicate.simplify();

		if (simplified instanceof NotBlockPredicate notBlockPredicate) {
			return notBlockPredicate.predicate;
		} else if (simplified instanceof AnyBlockPredicate) {
			return IBlockPosPredicate.none();
		} else if (simplified instanceof NoneBlockPredicate) {
			return IBlockPosPredicate.any();
		}
		return IBlockPosPredicate.super.simplify();
	}
}
