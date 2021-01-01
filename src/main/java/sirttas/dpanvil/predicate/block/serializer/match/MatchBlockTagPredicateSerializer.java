package sirttas.dpanvil.predicate.block.serializer.match;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;

public class MatchBlockTagPredicateSerializer extends BlockPosPredicateSerializer<MatchBlockTagPredicate> {

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
		json.addProperty(DPAnvilNames.TAG, predicate.getTagName().toString());
	}

	@Override
	public void write(MatchBlockTagPredicate predicate, PacketBuffer buf) {
		buf.writeResourceLocation(predicate.getTagName());
	}
}