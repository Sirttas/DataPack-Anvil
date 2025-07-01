package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class AnyBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "any";
	private static final AnyBlockPredicate INSTANCE = new AnyBlockPredicate();
	public static final MapCodec<AnyBlockPredicate> CODEC = MapCodec.unit(INSTANCE);

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

	@Override
	@Nonnull
	public List<Component> getTooltip() {
		return List.of(Component.translatable("tooltip.dpanvil.predicate.any"));
	}
}
