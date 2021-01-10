package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.Codecs;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class NotBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "not";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateType<NotBlockPredicate> TYPE;
	public static final Codec<NotBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codecs.BLOCK_PREDICATE.fieldOf(DPAnvilNames.VALUE).forGetter(NotBlockPredicate::getPredicate)
	).apply(builder, NotBlockPredicate::new));

	protected final IBlockPosPredicate predicate;

	public NotBlockPredicate(IBlockPosPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(IWorldReader world, BlockPos pos) {
		return !predicate.test(world, pos);
	}

	public IBlockPosPredicate getPredicate() {
		return predicate;
	}

	@Override
	public BlockPosPredicateType<NotBlockPredicate> getType() {
		return TYPE;
	}

	@Override
	public IBlockPosPredicate not() {
		return predicate;
	}
}