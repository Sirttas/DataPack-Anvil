package sirttas.dpanvil.api.predicate.block;

import com.google.gson.JsonElement;

import net.minecraft.network.PacketBuffer;

@Deprecated
public class BlockPosPredicateSerializer {

	private BlockPosPredicateSerializer() {}
	
	public static IBlockPosPredicate read(JsonElement json) {
		return IBlockPosPredicate.read(json);
	}

	public static IBlockPosPredicate read(PacketBuffer buf) {
		return IBlockPosPredicate.read(buf);
	}

	public static JsonElement write(IBlockPosPredicate predicate) {
		return predicate.write();
	}

	public static void writee(PacketBuffer buf, IBlockPosPredicate predicate) {
		predicate.write(buf);
	}

}
