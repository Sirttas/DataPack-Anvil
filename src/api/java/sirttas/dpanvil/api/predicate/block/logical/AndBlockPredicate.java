package sirttas.dpanvil.api.predicate.block.logical;

import java.util.Arrays;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.ListPredicate;

public class AndBlockPredicate extends ListPredicate {

	public static final String NAME = "and";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static Serializer<AndBlockPredicate> SERIALIZER;

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
	public Serializer<AndBlockPredicate> getSerializer() {
		return SERIALIZER;
	}
}
