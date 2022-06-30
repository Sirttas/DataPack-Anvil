package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public final class MatchBlockTagPredicate implements IBlockStatePredicate {

	public static final String NAME = "tag";
	public static final Codec<MatchBlockTagPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf(DPAnvilNames.TAG).forGetter(MatchBlockTagPredicate::getTag)
	).apply(builder, MatchBlockTagPredicate::new));

	private final TagKey<Block> tag;

	public MatchBlockTagPredicate(ResourceLocation tagName) {
		this(TagKey.create(Registry.BLOCK_REGISTRY, tagName));
	}

	public MatchBlockTagPredicate(TagKey<Block> tag) {
		this.tag = tag;
	}

	public TagKey<Block> getTag() {
		return tag;
	}

	@Override
	public boolean test(BlockState state) {
		return state.is(this.tag);
	}

	@Override
	public BlockPosPredicateType<MatchBlockTagPredicate> getType() {
		return BlockPosPredicateType.MATCH_TAG.get();
	}

}
