package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
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

	private final ITag<Block> tag;
	private final ResourceLocation tagName;

	public MatchBlockTagPredicate(ResourceLocation tagName) {
		this(tagName, getTag(tagName));
	}

	public MatchBlockTagPredicate( INamedTag<Block> tag) {
		this(tag.getName(), tag);
	}

	private MatchBlockTagPredicate(ResourceLocation tagName, ITag<Block> tag) {
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

	private static ITag<Block> getTag(ResourceLocation loc) {
		ITag<Block> tag = BlockTags.getAllTags().getTag(loc);

		if (tag == null) {
			tag = TagCollectionManager.getInstance().getBlocks().getTag(loc);
		}
		return tag;
	}

}
