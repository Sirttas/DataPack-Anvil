package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public record MatchBlockStatePredicate(
		BlockState state
) implements IBlockStatePredicate {

	public static final String NAME = "blockstate";
	public static final Codec<MatchBlockStatePredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			BlockState.CODEC.fieldOf(DPAnvilNames.STATE).forGetter(p -> p.state)
	).apply(builder, MatchBlockStatePredicate::new));

	@Override
	public boolean test(BlockState state) {
		return this.state.equals(state);
	}

	@Override
	public BlockPosPredicateType<MatchBlockStatePredicate> getType() {
		return BlockPosPredicateType.MATCH_STATE.get();
	}

}
