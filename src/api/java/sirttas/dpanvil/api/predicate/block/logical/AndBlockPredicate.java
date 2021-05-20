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
	public boolean test(IWorldReader world, BlockPos pos) {
		return predicates.stream().allMatch(predicate -> predicate.test(world, pos));
	}

	@Override
	public BlockPosPredicateType<AndBlockPredicate> getType() {
		return TYPE;
	}

	@Override
	public IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		return new AndBlockPredicate(this.merge(Lists.newArrayList(predicates), AndBlockPredicate.class));
	}

}
