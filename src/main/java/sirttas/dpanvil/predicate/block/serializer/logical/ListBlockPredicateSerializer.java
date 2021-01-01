package sirttas.dpanvil.predicate.block.serializer.logical;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateSerializer;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;
import sirttas.dpanvil.api.predicate.block.logical.ListBlockPredicate;

public class ListBlockPredicateSerializer<T extends ListBlockPredicate> extends BlockPosPredicateSerializer<T> {

	private final Function<Iterable<IBlockPosPredicate>, T> builder;

	public ListBlockPredicateSerializer(Function<Iterable<IBlockPosPredicate>, T> builder) {
		this.builder = builder;
	}

	@Override
	public T read(JsonObject json) {
		JsonArray values = JSONUtils.getJsonArray(json, DPAnvilNames.VALUES);

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

		predicate.getPredicates().stream().map(BlockPosPredicateSerializer::writePredicate).forEach(array::add);
		json.add(DPAnvilNames.VALUES, array);
	}

	@Override
	public void write(T predicate, PacketBuffer buf) {
		buf.writeInt(predicate.getPredicates().size());
		predicate.getPredicates().forEach(p -> BlockPosPredicateSerializer.writePredicate(buf, p));
	}
}