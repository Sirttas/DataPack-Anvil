package sirttas.dpanvil.api.predicate.block.logical;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.codec.Codecs;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public abstract class ListBlockPredicate implements IBlockPosPredicate {

	protected final List<IBlockPosPredicate> predicates;

	protected static <T extends ListBlockPredicate> Codec<T> codec(Function<List<IBlockPosPredicate>, T> builder) {
		return RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
				Codecs.BLOCK_PREDICATE.listOf().fieldOf(DPAnvilNames.VALUES).forGetter(ListBlockPredicate::getPredicates)
		).apply(codecBuilder, builder));
	}

	public ListBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		this.predicates = ImmutableList.copyOf(predicates);
	}

	public List<IBlockPosPredicate> getPredicates() {
		return predicates;
	}
}