package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public final class NoneBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "none";
	@Deprecated
	public static final NoneBlockPredicate INSTANCE = new NoneBlockPredicate();
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<NoneBlockPredicate> TYPE = null;
	public static final Codec<NoneBlockPredicate> CODEC = Codec.unit(INSTANCE);

	private NoneBlockPredicate() {
	}

	@Override
	public boolean test(LevelReader world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockPosPredicateType<NoneBlockPredicate> getType() {
		return TYPE;
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