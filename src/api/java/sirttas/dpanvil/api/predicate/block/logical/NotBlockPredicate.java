package sirttas.dpanvil.api.predicate.block.logical;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public final class NotBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "not";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<NotBlockPredicate> TYPE = null;
	public static final Codec<NotBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(NotBlockPredicate::getPredicate)
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
	
	@Override
	public IBlockPosPredicate simplify() {
		IBlockPosPredicate simplified = this.predicate.simplify();
		
		if (simplified instanceof NotBlockPredicate) {
			return ((NotBlockPredicate) simplified).predicate;
		} else if (simplified instanceof AnyBlockPredicate) {
			return IBlockPosPredicate.none();
		} else if (simplified instanceof NoneBlockPredicate) {
			return IBlockPosPredicate.any();
		}
		return IBlockPosPredicate.super.simplify();
	}
}