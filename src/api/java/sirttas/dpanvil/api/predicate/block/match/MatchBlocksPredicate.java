package sirttas.dpanvil.api.predicate.block.match;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.Codecs;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public final class MatchBlocksPredicate implements IBlockStatePredicate {

	public static final String NAME = "blocks";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<MatchBlocksPredicate> TYPE = null;
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
		return TYPE;
	}

}
