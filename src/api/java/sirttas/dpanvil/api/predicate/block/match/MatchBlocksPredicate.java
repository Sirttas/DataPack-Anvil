package sirttas.dpanvil.api.predicate.block.match;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
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

public class MatchBlocksPredicate implements IBlockStatePredicate {

	public static final String NAME = "blocks";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static Serializer SERIALIZER;

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

	@Override
	public Serializer getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends BlockPosPredicateSerializer<MatchBlocksPredicate> {

		@Override
		public MatchBlocksPredicate read(JsonObject json) {
			return new MatchBlocksPredicate(StreamSupport.stream(JSONUtils.getJsonArray(json, DPAnvilNames.BLOCKS).spliterator(),
					false)
					.map(j -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(j.getAsString()))).collect(Collectors.toList()));
		}

		@Override
		public MatchBlocksPredicate read(PacketBuffer buf) {
			int size = buf.readInt();
			List<Block> blocks = new ArrayList<>(size);
			
			IntStream.range(0, size).forEach(i -> blocks.add(ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation())));
			return new MatchBlocksPredicate(blocks);
		}

		@Override
		public void write(MatchBlocksPredicate predicate, JsonObject json) {
			JsonArray array = new JsonArray();

			predicate.blocks.stream().map(block -> block.getRegistryName().toString()).forEach(array::add);
			json.add(DPAnvilNames.BLOCKS, array);
		}

		@Override
		public void write(MatchBlocksPredicate predicate, PacketBuffer buf) {
			buf.writeInt(predicate.blocks.size());
			predicate.blocks.forEach(block -> buf.writeResourceLocation(block.getRegistryName()));
		}
	}

}
