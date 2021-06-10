package sirttas.dpanvil.api.predicate.block.logical;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;

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
	
	@Override
	public IBlockPosPredicate simplify() {
		List<IBlockPosPredicate> simplified = this.predicates.stream()
				.map(IBlockPosPredicate::simplify)
				.flatMap(p -> p instanceof OrBlockPredicate ? ((OrBlockPredicate) p).predicates.stream() : Stream.of(p))
				.filter(p -> !(p instanceof NoneBlockPredicate))
				.collect(Collectors.toList());
		
		if (simplified.isEmpty()) {
			return IBlockPosPredicate.none();
		} else if (simplified.stream().anyMatch(AnyBlockPredicate.class::isInstance)) {
			return IBlockPosPredicate.any();
		} else if (simplified.size() == 1) {
			return simplified.get(0);
		} else if (simplified.stream().allMatch(p -> p instanceof MatchBlockPredicate || p instanceof MatchBlocksPredicate)) {
			return new MatchBlocksPredicate(simplified.stream()
					.flatMap(p -> p instanceof MatchBlockPredicate ? Stream.of(((MatchBlockPredicate) p).getBlock()) : ((MatchBlocksPredicate) p).getBlocks().stream())
					.collect(Collectors.toList())).simplify();
		}
		return new OrBlockPredicate(simplified);
	}
}
