package sirttas.dpanvil.api.predicate.block.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.logical.AnyBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NoneBlockPredicate;

public record OffsetBlockPredicate(
		IBlockPosPredicate predicate,
		Vec3i offset
) implements IBlockPosPredicate {

	public static final String NAME = "offset";
	public static final Codec<OffsetBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(OffsetBlockPredicate::predicate),
			Vec3i.CODEC.fieldOf(DPAnvilNames.OFFSET).forGetter(OffsetBlockPredicate::offset)
	).apply(builder, OffsetBlockPredicate::new));

	@Override
	public boolean test(LevelReader world, BlockPos pos) {
		return predicate.test(world, pos.offset(offset));
	}

	@Override
	public IBlockPosPredicate offset(Vec3i offset) {
        return new OffsetBlockPredicate(this, this.offset.offset(offset));
    }

	@Override
	public BlockPosPredicateType<OffsetBlockPredicate> getType() {
		return BlockPosPredicateType.OFFSET.get();
	}

	@Override
	public IBlockPosPredicate simplify() {
		IBlockPosPredicate simplified = this.predicate.simplify();

		if (simplified instanceof OffsetBlockPredicate offsetBlockPredicate) {
			return new OffsetBlockPredicate(predicate, offset.offset(offsetBlockPredicate.offset())).simplify();
		} else if (simplified instanceof AnyBlockPredicate) {
            return IBlockPosPredicate.any();
        } else if (simplified instanceof NoneBlockPredicate) {
            return IBlockPosPredicate.none();
        } else if (Vec3i.ZERO.equals(offset)) {
            return simplified;
        }
		return IBlockPosPredicate.super.simplify();
	}
}
