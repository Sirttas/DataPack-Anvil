package sirttas.dpanvil.api.predicate.block.logical;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class NotBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "not";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateSerializer<NotBlockPredicate> SERIALIZER;

	protected final IBlockPosPredicate predicate;

	public NotBlockPredicate(IBlockPosPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(IWorldReader world, BlockPos pos) {
		return !predicate.test(world, pos);
	}

	public IBlockPosPredicate getPredicate() {
		return predicate;
	}

	@Override
	public BlockPosPredicateSerializer<NotBlockPredicate> getSerializer() {
		return SERIALIZER;
	}
}