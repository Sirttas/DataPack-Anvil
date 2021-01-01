package sirttas.dpanvil.predicate.block.serializer.match;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;

public class MatchBlockPredicateSerializer extends BlockPosPredicateSerializer<MatchBlockPredicate> {

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
		json.addProperty(DPAnvilNames.BLOCK, predicate.getBlock().getRegistryName().toString());
	}

	@Override
	public void write(MatchBlockPredicate predicate, PacketBuffer buf) {
		buf.writeResourceLocation(predicate.getBlock().getRegistryName());
	}
}