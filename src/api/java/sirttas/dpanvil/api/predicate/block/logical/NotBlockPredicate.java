package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public record NotBlockPredicate(
		IBlockPosPredicate predicate
) implements IBlockPosPredicate {

	public static final String NAME = "not";
	public static final BlockPosPredicateType<NotBlockPredicate> TYPE = null;
	public static final Codec<NotBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(NotBlockPredicate::predicate)
	).apply(builder, NotBlockPredicate::new));

	@Override
	public boolean test(LevelReader world, BlockPos pos) {
		return !predicate.test(world, pos);
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
