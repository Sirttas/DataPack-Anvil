package sirttas.dpanvil.api.predicate.block.logical;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractListBlockPredicate implements IBlockPosPredicate {

	protected final List<IBlockPosPredicate> predicates;

	protected static <T extends AbstractListBlockPredicate> Codec<T> codec(Function<List<IBlockPosPredicate>, T> builder) {
		return RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
				CODEC.listOf().fieldOf(DPAnvilNames.VALUES).forGetter(AbstractListBlockPredicate::getPredicates)
		).apply(codecBuilder, builder));
	}

	protected AbstractListBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		this.predicates = ImmutableList.copyOf(predicates);
	}

	public List<IBlockPosPredicate> getPredicates() {
		return predicates;
	}
	
	protected <T extends AbstractListBlockPredicate> List<IBlockPosPredicate> merge(Collection<IBlockPosPredicate> predicates, Class<T> type) {
		return Stream.concat(predicates.stream(), this.predicates.stream()).flatMap(predicate -> {
			if (type.isInstance(predicate)) {
				return type.cast(predicate).predicates.stream();
			}
			return Stream.of(predicate);
		}).toList();
	}
}
