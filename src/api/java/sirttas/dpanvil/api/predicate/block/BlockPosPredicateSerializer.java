package sirttas.dpanvil.api.predicate.block;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.data.IDataSerializer;

@SuppressWarnings("unchecked")
public abstract class BlockPosPredicateSerializer<T extends IBlockPosPredicate> extends ForgeRegistryEntry<BlockPosPredicateSerializer<?>> implements IDataSerializer<T>, JsonSerializer<T> {

	public static final IForgeRegistry<BlockPosPredicateSerializer<?>> REGISTRY = RegistryManager.ACTIVE.getRegistry(BlockPosPredicateSerializer.class);

	private static <T extends IBlockPosPredicate> BlockPosPredicateSerializer<T> getSerializer(T predicate) {
		return (BlockPosPredicateSerializer<T>) predicate.getSerializer();
	}

	private static <T extends IBlockPosPredicate> BlockPosPredicateSerializer<T> getSerializer(ResourceLocation id) {
		BlockPosPredicateSerializer<?> serializer = REGISTRY.getValue(id);

		if (serializer != null) {
			return (BlockPosPredicateSerializer<T>) serializer;
		}
		throw new JsonSyntaxException("Invalid or unsupported predicate serializer '" + id + "'");
	}

	public static <T extends IBlockPosPredicate> T readPredicate(JsonObject json) {
		BlockPosPredicateSerializer<T> serializer = getSerializer(new ResourceLocation(JSONUtils.getString(json, DPAnvilNames.TYPE)));

		return serializer.read(json);
	}

	public static <T extends IBlockPosPredicate> T readPredicate(PacketBuffer buf) {
		BlockPosPredicateSerializer<T> serializer = getSerializer(buf.readResourceLocation());

		return serializer.read(buf);
	}

	public static <T extends IBlockPosPredicate> JsonObject writePredicate(T predicate) {
		BlockPosPredicateSerializer<T> serializer = getSerializer(predicate);
		JsonObject json = new JsonObject();

		serializer.writeWithType(predicate, json);
		return json;
	}

	public static <T extends IBlockPosPredicate> void writePredicate(PacketBuffer buf, T predicate) {
		BlockPosPredicateSerializer<T> serializer = getSerializer(predicate);

		buf.writeResourceLocation(serializer.getRegistryName());
		serializer.write(predicate, buf);
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		return read((JsonObject) json);
	}

	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();

		writeWithType(src, json);
		return json;
	}

	private final void writeWithType(T predicate, JsonObject json) {
		json.addProperty(DPAnvilNames.TYPE, getRegistryName().toString());
		write(predicate, json);
	}

	public abstract void write(T predicate, JsonObject json);

}
