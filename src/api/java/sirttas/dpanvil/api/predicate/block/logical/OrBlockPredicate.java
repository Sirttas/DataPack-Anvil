package sirttas.dpanvil.api.predicate.block.logical;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class OrBlockPredicate extends ListBlockPredicate {

	public static final String NAME = "or";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateType<OrBlockPredicate> TYPE;
	public static final Codec<OrBlockPredicate> CODEC = codec(OrBlockPredicate::new);

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
	public BlockPosPredicateType<OrBlockPredicate> getType() {
		return TYPE;
	}

	@Override
	public IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		List<IBlockPosPredicate> list = Lists.newArrayList(predicates);

		list.addAll(this.predicates);
		return new OrBlockPredicate(list);
	}
}
