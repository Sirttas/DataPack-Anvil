package sirttas.dpanvil.api.predicate.block.match;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public class MatchBlockPredicate implements IBlockStatePredicate {

	public static final String NAME = "block";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateSerializer<MatchBlockPredicate> SERIALIZER;

	private final Block block;

	public MatchBlockPredicate(Block block) {
		this.block = block;
	}

	@Override
	public boolean test(BlockState state) {
		return block == state.getBlock();
	}

	public Block getBlock() {
		return block;
	}

	@Override
	public BlockPosPredicateSerializer<MatchBlockPredicate> getSerializer() {
		return SERIALIZER;
	}

}
