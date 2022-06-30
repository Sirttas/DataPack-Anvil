package sirttas.dpanvil.api.predicate.block.match;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.codec.Codecs;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

import java.util.List;

public final class MatchBlocksPredicate implements IBlockStatePredicate {

	public static final String NAME = "blocks";
	public static final Codec<MatchBlocksPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codecs.BLOCK.listOf().fieldOf(DPAnvilNames.BLOCKS).forGetter(MatchBlocksPredicate::getBlocks)
	).apply(builder, MatchBlocksPredicate::new));

	private final List<Block> blocks;

	public MatchBlocksPredicate(Block... blocks) {
		this.blocks = ImmutableList.copyOf(blocks);
	}

	public MatchBlocksPredicate(Iterable<Block> blocks) {
		this.blocks = ImmutableList.copyOf(blocks);
	}

	@Override
	public boolean test(BlockState state) {
		return blocks.contains(state.getBlock());
	}

	public List<Block> getBlocks() {
		return blocks;
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
			return new MatchBlockPredicate(blocks.get(0));
		}
		return new MatchBlocksPredicate(blocks.stream()
				.distinct()
				.toList());
	}
	
}
