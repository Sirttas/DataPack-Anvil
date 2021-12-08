package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class AndBlockPredicate extends AbstractListBlockPredicate {

	public static final String NAME = "and";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<AndBlockPredicate> TYPE = null;
	public static final Codec<AndBlockPredicate> CODEC = codec(AndBlockPredicate::new);

	public AndBlockPredicate(IBlockPosPredicate... predicates) {
		this(Arrays.asList(predicates));
	}

	public AndBlockPredicate(Iterable<IBlockPosPredicate> predicates) {
		super(predicates);
	}

	@Override
	public boolean test(LevelReader world, BlockPos pos) {
		return predicates.stream().allMatch(predicate -> predicate.test(world, pos));
	}

	@Override
	public BlockPosPredicateType<AndBlockPredicate> getType() {
		return TYPE;
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
