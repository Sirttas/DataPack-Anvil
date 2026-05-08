package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import java.util.List;

public final class NoneBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "none";
	private static final NoneBlockPredicate INSTANCE = new NoneBlockPredicate();
	public static final MapCodec<NoneBlockPredicate> CODEC = MapCodec.unit(INSTANCE);

	private NoneBlockPredicate() {
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, @Nullable Direction direction) {
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

	@Override
    public List<Component> getTooltip() {
		return List.of(Component.translatable("tooltip.dpanvil.predicate.none"));
	}
}
