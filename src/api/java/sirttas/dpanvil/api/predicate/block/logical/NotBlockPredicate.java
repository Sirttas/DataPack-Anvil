package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateTooltipHelper;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import java.util.List;

public record NotBlockPredicate(
		IBlockPosPredicate predicate
) implements IBlockPosPredicate {

	public static final String NAME = "not";
	public static final MapCodec<NotBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(NotBlockPredicate::predicate)
	).apply(builder, NotBlockPredicate::new));

	@Override
	public boolean test(LevelReader level, BlockPos pos, @Nullable Direction direction) {
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

        return switch (simplified) {
            case NotBlockPredicate(IBlockPosPredicate child) -> child;
            case AnyBlockPredicate _ -> IBlockPosPredicate.none();
            case NoneBlockPredicate _ -> IBlockPosPredicate.any();
            default -> IBlockPosPredicate.super.simplify();
        };
    }

	@Override
    public List<Component> getTooltip() {
		return BlockPosPredicateTooltipHelper.not(predicate, IBlockPosPredicate::getTooltip);
	}
}
