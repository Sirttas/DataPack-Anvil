package sirttas.dpanvil.api.predicate.block.match;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

public class MatchBlockPredicate implements IBlockStatePredicate {

	public static final String NAME = "block";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static Serializer SERIALIZER;

	private final Block block;

	public MatchBlockPredicate(Block block) {
		this.block = block;
	}

	@Override
	public boolean test(BlockState state) {
		return block == state.getBlock();
	}

	@Override
	public Serializer getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends BlockPosPredicateSerializer<MatchBlockPredicate> {

		@Override
		public MatchBlockPredicate read(JsonObject json) {
			return createFromName(new ResourceLocation(JSONUtils.getString(json, DPAnvilNames.BLOCK)));
		}

		@Override
		public MatchBlockPredicate read(PacketBuffer buf) {
			return createFromName(buf.readResourceLocation());
		}

		private MatchBlockPredicate createFromName(ResourceLocation id) {
			return new MatchBlockPredicate(ForgeRegistries.BLOCKS.getValue(id));
		}

		@Override
		public void write(MatchBlockPredicate predicate, JsonObject json) {
			json.addProperty(DPAnvilNames.BLOCK, predicate.block.getRegistryName().toString());
		}

		@Override
		public void write(MatchBlockPredicate predicate, PacketBuffer buf) {
			buf.writeResourceLocation(predicate.block.getRegistryName());
		}
	}

}
