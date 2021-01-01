package sirttas.dpanvil.api.predicate.block.match;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public class MatchBlockTagPredicate implements IBlockStatePredicate {

	public static final String NAME = "tag";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static BlockPosPredicateSerializer<MatchBlockTagPredicate> SERIALIZER;

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
	public BlockPosPredicateSerializer<MatchBlockTagPredicate> getSerializer() {
		return SERIALIZER;
	}

	private static ITag<Block> getTag(ResourceLocation loc) {
		ITag<Block> tag = BlockTags.getCollection().get(loc);

		if (tag == null) {
			tag = TagCollectionManager.getManager().getBlockTags().get(loc);
		}
		return tag;
	}

}
