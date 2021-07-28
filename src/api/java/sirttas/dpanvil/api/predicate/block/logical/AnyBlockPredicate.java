package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public final class AnyBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "any";
	@Deprecated
	public static final AnyBlockPredicate INSTANCE = new AnyBlockPredicate();
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<AnyBlockPredicate> TYPE = null;
	public static final Codec<AnyBlockPredicate> CODEC = Codec.unit(INSTANCE);

	private AnyBlockPredicate() {}

	@Override
	public boolean test(LevelReader world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockPosPredicateType<AnyBlockPredicate> getType() {
		return TYPE;
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