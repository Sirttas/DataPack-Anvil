package sirttas.dpanvil.api.predicate.block;

import java.util.List;
import java.util.function.BiPredicate;

import com.google.common.collect.Lists;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;

public interface IBlockPosPredicate {

	boolean test(IWorldReader world, BlockPos pos);

	default BiPredicate<IWorldReader, BlockPos> asBlockPosPredicate() {
		return this::test;
	}

	default IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		List<IBlockPosPredicate> list = Lists.newArrayList(predicates);

		list.add(this);
		return new OrBlockPredicate(list);
	}

	default IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		List<IBlockPosPredicate> list = Lists.newArrayList(predicates);
		
		list.add(this);
		return new AndBlockPredicate(list);
	}

	default IBlockPosPredicate not() {
		return new NotBlockPredicate(this);
	}

	BlockPosPredicateSerializer<? extends IBlockPosPredicate> getSerializer();

}
