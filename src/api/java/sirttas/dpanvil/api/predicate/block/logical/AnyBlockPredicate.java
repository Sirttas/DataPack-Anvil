package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class AnyBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "any";
	public static final AnyBlockPredicate INSTANCE = new AnyBlockPredicate();
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateType<AnyBlockPredicate> TYPE;
	public static final Codec<AnyBlockPredicate> CODEC = Codec.unit(INSTANCE);

	private AnyBlockPredicate() {
	}

	@Override
	public boolean test(IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockPosPredicateType<AnyBlockPredicate> getType() {
		return TYPE;
	}

	@Override
	public IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		return INSTANCE;
	}
}