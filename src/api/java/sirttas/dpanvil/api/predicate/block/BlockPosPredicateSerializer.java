package sirttas.dpanvil.api.predicate.block;

import com.google.gson.JsonElement;

import net.minecraft.network.PacketBuffer;
import sirttas.dpanvil.api.codec.CodecHelper;

public class BlockPosPredicateSerializer {

	public static IBlockPosPredicate readPredicate(JsonElement json) {
		return CodecHelper.decode(BlockPredicates.CODEC, json);
	}

	public static IBlockPosPredicate readPredicate(PacketBuffer buf) {
		return CodecHelper.decode(BlockPredicates.CODEC, buf);
	}

	public static JsonElement writePredicate(IBlockPosPredicate predicate) {
		return CodecHelper.encode(BlockPredicates.CODEC, predicate);
	}

	public static void writePredicate(PacketBuffer buf, IBlockPosPredicate predicate) {
		CodecHelper.encode(BlockPredicates.CODEC, predicate, buf);
	}

}
