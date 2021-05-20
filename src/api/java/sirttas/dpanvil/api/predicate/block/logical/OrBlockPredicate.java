package sirttas.dpanvil.api.predicate.block.logical;

import java.util.Arrays;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public final class OrBlockPredicate extends AbstractListBlockPredicate {

	public static final String NAME = "or";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<OrBlockPredicate> TYPE = null;
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
		return new OrBlockPredicate(this.merge(Lists.newArrayList(predicates), OrBlockPredicate.class));
	}
}
