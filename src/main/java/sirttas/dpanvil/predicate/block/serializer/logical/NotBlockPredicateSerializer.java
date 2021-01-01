package sirttas.dpanvil.predicate.block.serializer.logical;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;

public class NotBlockPredicateSerializer extends BlockPosPredicateSerializer<NotBlockPredicate> {

	@Override
	public NotBlockPredicate read(JsonObject json) {
		return new NotBlockPredicate(BlockPosPredicateSerializer.readPredicate(JSONUtils.getJsonObject(json, DPAnvilNames.VALUE)));
	}

	@Override
	public NotBlockPredicate read(PacketBuffer buf) {
		return new NotBlockPredicate(BlockPosPredicateSerializer.readPredicate(buf));
	}

	@Override
	public void write(NotBlockPredicate predicate, JsonObject json) {
		json.add(DPAnvilNames.VALUE, BlockPosPredicateSerializer.writePredicate(predicate.getPredicate()));
	}

	@Override
	public void write(NotBlockPredicate predicate, PacketBuffer buf) {
		BlockPosPredicateSerializer.writePredicate(buf, predicate.getPredicate());
	}
}