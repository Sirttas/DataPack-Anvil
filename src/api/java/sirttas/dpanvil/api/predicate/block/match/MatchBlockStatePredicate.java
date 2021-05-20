package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public final class MatchBlockStatePredicate implements IBlockStatePredicate {

	public static final String NAME = "blockstate";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<MatchBlockStatePredicate> TYPE = null;
	public static final Codec<MatchBlockStatePredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			BlockState.CODEC.fieldOf(DPAnvilNames.STATE).forGetter(p -> p.state)
	).apply(builder, MatchBlockStatePredicate::new));

	private final BlockState state;

	public MatchBlockStatePredicate(BlockState state) {
		this.state = state;
	}

	@Override
	public boolean test(BlockState state) {
		return this.state.equals(state);
	}

	@Override
	public BlockPosPredicateType<MatchBlockStatePredicate> getType() {
		return TYPE;
	}

}
