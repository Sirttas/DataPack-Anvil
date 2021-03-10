package sirttas.dpanvil.api.predicate.block.match;

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

public final class MatchBlockPredicate implements IBlockStatePredicate {

	public static final String NAME = "block";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateType<MatchBlockPredicate> TYPE;
	public static final Codec<MatchBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codecs.BLOCK.fieldOf(DPAnvilNames.BLOCK).forGetter(MatchBlockPredicate::getBlock)
	).apply(builder, MatchBlockPredicate::new));


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
	public BlockPosPredicateType<MatchBlockPredicate> getType() {
		return TYPE;
	}

}
