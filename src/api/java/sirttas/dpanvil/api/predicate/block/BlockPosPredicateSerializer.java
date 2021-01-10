package sirttas.dpanvil.api.predicate.block;

import com.google.gson.JsonElement;

import net.minecraft.network.PacketBuffer;
import sirttas.dpanvil.api.codec.CodecHelper;

public class BlockPosPredicateSerializer {

	public static IBlockPosPredicate read(JsonElement json) {
		return CodecHelper.decode(BlockPosPredicates.CODEC, json);
	}

	public static IBlockPosPredicate read(PacketBuffer buf) {
		return CodecHelper.decode(BlockPosPredicates.CODEC, buf);
	}

	public static JsonElement write(IBlockPosPredicate predicate) {
		return CodecHelper.encode(BlockPosPredicates.CODEC, predicate);
	}

	public static void writee(PacketBuffer buf, IBlockPosPredicate predicate) {
		CodecHelper.encode(BlockPosPredicates.CODEC, predicate, buf);
	}

}
