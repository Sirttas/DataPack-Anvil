package sirttas.dpanvil.api.predicate.block.logical;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public class NotBlockPredicate implements IBlockPosPredicate {

	public static final String NAME = "not";
	@ObjectHolder(DataPackAnvilApi.MODID + ":" + NAME) public static Serializer SERIALIZER;

	protected final IBlockPosPredicate predicate;

	public NotBlockPredicate(IBlockPosPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(IWorldReader world, BlockPos pos) {
		return !predicate.test(world, pos);
	}

	@Override
	public Serializer getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends BlockPosPredicateSerializer<NotBlockPredicate> {

		@Override
		public NotBlockPredicate read(JsonObject json) {
			return new NotBlockPredicate(BlockPosPredicateSerializer.readPredicate(JSONUtils.getJsonObject(json, "value")));
		}

		@Override
		public NotBlockPredicate read(PacketBuffer buf) {
			return new NotBlockPredicate(BlockPosPredicateSerializer.readPredicate(buf));
		}

		@Override
		public void write(NotBlockPredicate predicate, JsonObject json) {
			json.add("value", BlockPosPredicateSerializer.writePredicate(predicate.predicate));
		}

		@Override
		public void write(NotBlockPredicate predicate, PacketBuffer buf) {
			BlockPosPredicateSerializer.writePredicate(buf, predicate.predicate);
		}
	}
}