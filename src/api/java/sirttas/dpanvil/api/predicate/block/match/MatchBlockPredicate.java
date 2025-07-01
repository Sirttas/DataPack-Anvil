package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

import javax.annotation.Nonnull;
import java.util.List;

public record MatchBlockPredicate(
		Block block
) implements IBlockStatePredicate {

	public static final String NAME = "block";
	public static final MapCodec<MatchBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf(DPAnvilNames.BLOCK).forGetter(MatchBlockPredicate::block)
	).apply(builder, MatchBlockPredicate::new));


	@Override
	public boolean test(BlockState state) {
		return state.is(block);
	}

	@Override
	public BlockPosPredicateType<MatchBlockPredicate> getType() {
		return BlockPosPredicateType.MATCH_BLOCK.get();
	}

	@Override
	@Nonnull
	public List<Component> getTooltip() {
		return List.of(block.getName());
	}
}
