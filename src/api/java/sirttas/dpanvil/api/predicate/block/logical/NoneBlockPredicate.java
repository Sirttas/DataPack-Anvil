package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class NoneBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "none";
	public static final NoneBlockPredicate INSTANCE = new NoneBlockPredicate();
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateType<NoneBlockPredicate> TYPE;
	public static final Codec<NoneBlockPredicate> CODEC = Codec.unit(INSTANCE);

	private NoneBlockPredicate() {
	}

	@Override
	public boolean test(IWorldReader world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockPosPredicateType<NoneBlockPredicate> getType() {
		return TYPE;
	}

	@Override
	public IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		return INSTANCE;
	}
}