package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class AndBlockPredicate extends AbstractListBlockPredicate {

	public static final String NAME = "and";
	public static final Codec<AndBlockPredicate> CODEC = codec(AndBlockPredicate::new);

	public AndBlockPredicate(IBlockPosPredicate... predicates) {
		this(Arrays.asList(predicates));
	}

	public AndBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		super(predicates);
	}

	@Override
	public boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
		return predicates.stream().allMatch(predicate -> predicate.test(level, pos, direction));
	}

	@Override
	public BlockPosPredicateType<AndBlockPredicate> getType() {
		return BlockPosPredicateType.AND.get();
	}

	@Override
	public IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		return new AndBlockPredicate(this.merge(List.of(predicates), AndBlockPredicate.class));
	}

	@Override
	public IBlockPosPredicate simplify() {
		List<IBlockPosPredicate> simplified = this.predicates.stream()
				.map(IBlockPosPredicate::simplify)
				.flatMap(p -> p instanceof AndBlockPredicate andBlockPredicate ? andBlockPredicate.predicates.stream() : Stream.of(p))
				.filter(p -> !(p instanceof AnyBlockPredicate))
				.toList();
		
		if (simplified.isEmpty() || simplified.stream().anyMatch(NoneBlockPredicate.class::isInstance)) {
			return IBlockPosPredicate.none();
		} else if (simplified.size() == 1) {
			return simplified.get(0);
		}
		return new AndBlockPredicate(simplified);
	}
}
