package sirttas.dpanvil.api.predicate.block.logical;

import java.util.Arrays;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class OrBlockPredicate extends ListBlockPredicate {

	public static final String NAME = "or";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateSerializer<OrBlockPredicate> SERIALIZER;

	public OrBlockPredicate(IBlockPosPredicate... predicates) {
		this(Arrays.asList(predicates));
	}

	public OrBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		super(predicates);
	}

	@Override
	public boolean test(IWorldReader world, BlockPos pos) {
		return predicates.stream().anyMatch(predicate -> predicate.test(world, pos));
	}

	@Override
	public BlockPosPredicateSerializer<OrBlockPredicate> getSerializer() {
		return SERIALIZER;
	}
}
