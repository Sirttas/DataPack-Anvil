package sirttas.dpanvil.predicate.block.serializer.match;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;

public class MatchBlocksPredicateSerializer extends BlockPosPredicateSerializer<MatchBlocksPredicate> {

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

		predicate.getBlocks().stream().map(block -> block.getRegistryName().toString()).forEach(array::add);
		json.add(DPAnvilNames.BLOCKS, array);
	}

	@Override
	public void write(MatchBlocksPredicate predicate, PacketBuffer buf) {
		buf.writeInt(predicate.getBlocks().size());
		predicate.getBlocks().forEach(block -> buf.writeResourceLocation(block.getRegistryName()));
	}
}