package sirttas.dpanvil.api.predicate.block.logical;

import java.util.List;

import com.google.common.collect.ImmutableList;

import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public abstract class ListBlockPredicate implements IBlockPosPredicate {

	protected final List<IBlockPosPredicate> predicates;

	public ListBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		this.predicates = ImmutableList.copyOf(predicates);
	}

	public List<IBlockPosPredicate> getPredicates() {
		return predicates;
	}
}