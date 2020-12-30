package sirttas.dpanvil.api.predicate.block;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public abstract class ListPredicate implements IBlockPosPredicate {

	protected final List<IBlockPosPredicate> predicates;

	public ListPredicate(Iterable<IBlockPosPredicate> predicates) {
		this.predicates = ImmutableList.copyOf(predicates);
	}

	public static class Serializer<T extends ListPredicate> extends BlockPosPredicateSerializer<T> {

		private final Function<Iterable<IBlockPosPredicate>, T> builder;

		public Serializer(Function<Iterable<IBlockPosPredicate>, T> builder) {
			this.builder = builder;
		}

		@Override
		public T read(JsonObject json) {
			JsonArray values = JSONUtils.getJsonArray(json, "values");

			return builder.apply(StreamSupport.stream(values.spliterator(), false).map(this::readPredicate).collect(Collectors.toList()));
		}

		private IBlockPosPredicate readPredicate(JsonElement j) {
			return BlockPosPredicateSerializer.readPredicate((JsonObject) j);
		}

		@Override
		public T read(PacketBuffer buf) {
			int size = buf.readInt();
			List<IBlockPosPredicate> predicates = Lists.newArrayList();

			for (int i = 0; i < size; i++) {
				predicates.add(BlockPosPredicateSerializer.readPredicate(buf));
			}
			return builder.apply(predicates);
		}

		@Override
		public void write(T predicate, JsonObject json) {
			JsonArray array = new JsonArray();

			predicate.predicates.stream().map(BlockPosPredicateSerializer::writePredicate).forEach(array::add);
			json.add("values", array);
		}

		@Override
		public void write(T predicate, PacketBuffer buf) {
			buf.writeInt(predicate.predicates.size());
			predicate.predicates.forEach(p -> BlockPosPredicateSerializer.writePredicate(buf, p));
		}
	}
}