package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public final class MatchBlockTagPredicate implements IBlockStatePredicate {

	public static final String NAME = "tag";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static final BlockPosPredicateType<MatchBlockTagPredicate> TYPE = null;
	public static final Codec<MatchBlockTagPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			ResourceLocation.CODEC.fieldOf(DPAnvilNames.TAG).forGetter(MatchBlockTagPredicate::getTagName)
	).apply(builder, MatchBlockTagPredicate::new));

	private final Tag<Block> tag;
	private final ResourceLocation tagName;

	public MatchBlockTagPredicate(ResourceLocation tagName) {
		this(tagName, getTag(tagName));
	}

	public MatchBlockTagPredicate( Named<Block> tag) {
		this(tag.getName(), tag);
	}

	private MatchBlockTagPredicate(ResourceLocation tagName, Tag<Block> tag) {
		this.tagName = tagName;
		this.tag = tag;
	}

	@Override
	public boolean test(BlockState state) {
		return tag.contains(state.getBlock());
	}

	public ResourceLocation getTagName() {
		return tagName;
	}

	@Override
	public BlockPosPredicateType<MatchBlockTagPredicate> getType() {
		return TYPE;
	}

	private static Tag<Block> getTag(ResourceLocation loc) {
		return BlockTags.getAllTags().getTag(loc);
	}
}
