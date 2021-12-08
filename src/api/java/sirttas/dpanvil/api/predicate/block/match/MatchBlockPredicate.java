package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.Codecs;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public record MatchBlockPredicate(
		Block block
) implements IBlockStatePredicate {

	public static final String NAME = "block";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME)
	public static final BlockPosPredicateType<MatchBlockPredicate> TYPE = null;
	public static final Codec<MatchBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codecs.BLOCK.fieldOf(DPAnvilNames.BLOCK).forGetter(MatchBlockPredicate::block)
	).apply(builder, MatchBlockPredicate::new));


	@Override
	public boolean test(BlockState state) {
		return block == state.getBlock();
	}

	@Override
	public BlockPosPredicateType<MatchBlockPredicate> getType() {
		return TYPE;
	}

}
