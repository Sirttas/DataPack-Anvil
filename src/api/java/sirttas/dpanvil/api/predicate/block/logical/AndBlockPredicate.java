package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateTooltipHelper;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class AndBlockPredicate extends AbstractListBlockPredicate {

	public static final String NAME = "and";
	public static final MapCodec<AndBlockPredicate> CODEC = codec(AndBlockPredicate::new);

	public AndBlockPredicate(IBlockPosPredicate... predicates) {
		this(Arrays.asList(predicates));
	}

	public AndBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		super(predicates);
	}

	@Override
	public boolean test(LevelReader level, BlockPos pos, @Nullable Direction direction) {
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
			return simplified.getFirst();
		}
		return new AndBlockPredicate(simplified);
	}

	@Override
    public List<Component> getTooltip() {
		return BlockPosPredicateTooltipHelper.and(predicates, IBlockPosPredicate::getTooltip);
	}
}
