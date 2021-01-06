package sirttas.dpanvil.api.codec;

import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CodecHelper {

	/**
	 * Get a codec for a forge registry
	 * 
	 * @param <T>
	 * @param suplier
	 * @return
	 */
	public static <T extends IForgeRegistryEntry<T>> Codec<T> getRegistryCodec(Supplier<IForgeRegistry<T>> suplier) {
		return ResourceLocation.CODEC.comapFlatMap(id -> {
			IForgeRegistry<T> registry = suplier.get();
			T value = registry.getValue(id);
			
			return value != null ? DataResult.success(value) : DataResult.error(id.toString() + " is not present in registry: " + registry.getRegistryName().toString());
		}, IForgeRegistryEntry::getRegistryName);
	}

	public static <T> T decode(Decoder<T> decoder, JsonElement json) {
		return decode(decoder, JsonOps.INSTANCE, json);
	}

	public static <T> T decode(Decoder<T> decoder, PacketBuffer buf) {
		return decode(decoder, NBTDynamicOps.INSTANCE, buf.readCompoundTag());
	}

	public static <T, U> T decode(Decoder<T> decoder, DynamicOps<U> ops, U input) {
		return handleResult(decoder.decode(ops, input)).getFirst();
	}

	public static <T> void encode(Encoder<T> encoder, T data, PacketBuffer buf) {
		INBT nbt = encoder.encode(data, NBTDynamicOps.INSTANCE, NBTDynamicOps.INSTANCE.empty()).get().orThrow();

		if (nbt instanceof CompoundNBT) {
			buf.writeCompoundTag((CompoundNBT) nbt);
		}
	}

	public static <T> JsonElement encode(Encoder<T> encoder, T data) {
		return encode(encoder, JsonOps.INSTANCE, data);
	}

	public static <T, U> U encode(Encoder<T> encoder, DynamicOps<U> ops, T data) {
		return handleResult(encoder.encode(data, ops, ops.empty()));
	}

	public static <T> T handleResult(DataResult<T> result) {
		return result.get().orThrow();
	}
}
