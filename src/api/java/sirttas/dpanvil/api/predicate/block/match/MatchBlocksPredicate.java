package sirttas.dpanvil.api.predicate.block.match;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public class MatchBlocksPredicate implements IBlockStatePredicate {

	public static final String NAME = "blocks";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateSerializer<MatchBlocksPredicate> SERIALIZER;

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
	public BlockPosPredicateSerializer<MatchBlocksPredicate> getSerializer() {
		return SERIALIZER;
	}

}
