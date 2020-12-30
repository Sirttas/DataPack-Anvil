package sirttas.dpanvil.api.predicate.block.match;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public class MatchBlockTagPredicate implements IBlockStatePredicate {

	public static final String NAME = "tag";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static Serializer SERIALIZER;

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

	@Override
	public Serializer getSerializer() {
		return SERIALIZER;
	}

	private static ITag<Block> getTag(ResourceLocation loc) {
		ITag<Block> tag = BlockTags.getCollection().get(loc);

		if (tag == null) {
			tag = TagCollectionManager.getManager().getBlockTags().get(loc);
		}
		return tag;
	}

	public static class Serializer extends BlockPosPredicateSerializer<MatchBlockTagPredicate> {

		@Override
		public MatchBlockTagPredicate read(JsonObject json) {
			return new MatchBlockTagPredicate(new ResourceLocation(JSONUtils.getString(json, DPAnvilNames.TAG)));
		}

		@Override
		public MatchBlockTagPredicate read(PacketBuffer buf) {
			return new MatchBlockTagPredicate(buf.readResourceLocation());
		}

		@Override
		public void write(MatchBlockTagPredicate predicate, JsonObject json) {
			json.addProperty(DPAnvilNames.TAG, predicate.tagName.toString());
		}

		@Override
		public void write(MatchBlockTagPredicate predicate, PacketBuffer buf) {
			buf.writeResourceLocation(predicate.tagName);
		}
	}

}
