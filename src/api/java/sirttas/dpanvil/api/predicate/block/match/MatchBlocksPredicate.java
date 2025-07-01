package sirttas.dpanvil.api.predicate.block.match;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateTooltipHelper;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

import javax.annotation.Nonnull;
import java.util.List;

public record MatchBlocksPredicate(
		List<Block> blocks
) implements IBlockStatePredicate {

	public static final String NAME = "blocks";
	public static final MapCodec<MatchBlocksPredicate> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf(DPAnvilNames.BLOCKS).forGetter(MatchBlocksPredicate::blocks)
	).apply(builder, MatchBlocksPredicate::new));

	public MatchBlocksPredicate(Block... blocks) {
		this(ImmutableList.copyOf(blocks));
	}

	public MatchBlocksPredicate(Iterable<Block> blocks) {
		this(ImmutableList.copyOf(blocks));
	}

	@Override
	public boolean test(BlockState state) {
		return blocks.stream().anyMatch(state::is);
	}

	@Override
	public BlockPosPredicateType<MatchBlocksPredicate> getType() {
		return BlockPosPredicateType.MATCH_BLOCKS.get();
	}

	@Override
	public IBlockPosPredicate simplify() {
		if (blocks.isEmpty()) {
			return IBlockPosPredicate.none();
		} else if (blocks.size() == 1) {
			return new MatchBlockPredicate(blocks.getFirst());
		}
		return new MatchBlocksPredicate(blocks.stream()
				.distinct()
				.toList());
	}

	@Override
	@Nonnull
	public List<Component> getTooltip() {
		return BlockPosPredicateTooltipHelper.or(blocks, b -> List.of(b.getName()));
	}
}
