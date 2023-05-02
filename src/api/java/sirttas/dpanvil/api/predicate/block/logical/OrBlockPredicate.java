package sirttas.dpanvil.api.predicate.block.logical;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class OrBlockPredicate extends AbstractListBlockPredicate {

	public static final String NAME = "or";
	public static final Codec<OrBlockPredicate> CODEC = codec(OrBlockPredicate::new);

	public OrBlockPredicate(IBlockPosPredicate... predicates) {
		this(Arrays.asList(predicates));
	}

	public OrBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		super(predicates);
	}

	@Override
	public boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
		return predicates.stream().anyMatch(predicate -> predicate.test(level, pos, direction));
	}

	@Override
	public BlockPosPredicateType<OrBlockPredicate> getType() {
		return BlockPosPredicateType.OR.get();
	}

	@Override
	public IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		return new OrBlockPredicate(this.merge(Lists.newArrayList(predicates), OrBlockPredicate.class));
	}
	
	@Override
	public IBlockPosPredicate simplify() {
		List<IBlockPosPredicate> simplified = this.predicates.stream()
				.map(IBlockPosPredicate::simplify)
				.flatMap(p -> p instanceof OrBlockPredicate orBlockPredicate ? orBlockPredicate.predicates.stream() : Stream.of(p))
				.filter(p -> !(p instanceof NoneBlockPredicate))
				.toList();
		
		if (simplified.isEmpty()) {
			return IBlockPosPredicate.none();
		} else if (simplified.stream().anyMatch(AnyBlockPredicate.class::isInstance)) {
			return IBlockPosPredicate.any();
		} else if (simplified.size() == 1) {
			return simplified.get(0);
		} else if (simplified.stream().allMatch(p -> p instanceof MatchBlockPredicate || p instanceof MatchBlocksPredicate)) {
			return new MatchBlocksPredicate(simplified.stream()
					.flatMap(p -> p instanceof MatchBlockPredicate matchBlockPredicate ? Stream.of(matchBlockPredicate.block()) : ((MatchBlocksPredicate) p).getBlocks().stream())
					.toList()).simplify();
		}
		return new OrBlockPredicate(simplified);
	}
}
