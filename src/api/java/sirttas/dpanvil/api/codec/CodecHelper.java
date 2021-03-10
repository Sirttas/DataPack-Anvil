package sirttas.dpanvil.api.codec;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import sirttas.dpanvil.api.DataPackAnvilApi;

public class CodecHelper {

	/**
	 * Get a codec for a {@link IForgeRegistry}
	 * 
	 * @param <T>     the type of data inside the registry
	 * @param suplier a {@link Supplier} for the {@link IForgeRegistry}
	 * @return a Codec
	 */
	public static <T extends IForgeRegistryEntry<T>> Codec<T> getRegistryCodec(Supplier<IForgeRegistry<T>> suplier) {
		return ResourceLocation.CODEC.comapFlatMap(id -> {
			IForgeRegistry<T> registry = suplier.get();
			T value = registry.getValue(id);
			
			return value != null ? DataResult.success(value) : DataResult.error(id.toString() + " is not present in registry: " + registry.getRegistryName().toString());
		}, IForgeRegistryEntry::getRegistryName);
	}

	public static <T, F> Codec<T> remapField(Codec<T> codec, MapEncoder<F> fieldEncoder, Function<T, F> fieldGetter) {
		return Codec.of(remapEncoderField(codec, fieldEncoder, fieldGetter), codec);
	}

	public static <T, F> Codec<T> remapField(Codec<T> codec, MapDecoder<F> fieldDecoder, BiConsumer<T, F> fieldSetter) {
		return Codec.of(codec, remapDecoderField(codec, fieldDecoder, fieldSetter));
	}

	public static <T, F> Codec<T> remapField(Codec<T> codec, MapCodec<F> fieldCodec, Function<T, F> fieldGetter, BiConsumer<T, F> fieldSetter) {
		return Codec.of(remapEncoderField(codec, fieldCodec, fieldGetter), remapDecoderField(codec, fieldCodec, fieldSetter));
	}

	public static <T, F> Encoder<T> remapEncoderField(Encoder<T> encoder, MapEncoder<F> fieldEncoder, Function<T, F> fieldGetter) {
		return new Encoder<T>() {
			@Override
			public <U> DataResult<U> encode(T input, DynamicOps<U> ops, U prefix) {
				return encoder.encode(input, ops, prefix).flatMap(d -> fieldEncoder.encoder().encode(fieldGetter.apply(input), ops, d));
			}

			@Override
			public String toString() {
				return encoder.toString() + "FieldEncoderMapped [" + fieldEncoder.toString() + ']';
			}
		};
	}

	public static <T, F> Decoder<T> remapDecoderField(Decoder<T> decoder, MapDecoder<F> fieldDecoder, BiConsumer<T, F> fieldSetter) {
		return new Decoder<T>() {
			@Override
			public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> ops, U input) {
				return decoder.decode(ops, input).flatMap(pair -> fieldDecoder.decoder().decode(ops, input).map(fieldPair -> {
					fieldSetter.accept(pair.getFirst(), fieldPair.getFirst());
					return pair;
				}));
			}

			@Override
			public String toString() {
				return decoder.toString() + "FieldDecoderMapped [" + fieldDecoder.toString() + ']';
			}
		};
	}

	public static <T> T decode(Decoder<T> decoder, JsonElement json) {
		return decode(decoder, JsonOps.INSTANCE, json);
	}

	public static <T> T decode(Decoder<T> decoder, PacketBuffer buf) {
		return decode(decoder, buf.readCompoundTag());
	}

	public static <T> T decode(Decoder<T> decoder, INBT nbt) {
		return decode(decoder, NBTDynamicOps.INSTANCE, nbt);
	}

	public static <T, U> T decode(Decoder<T> decoder, DynamicOps<U> ops, U input) {
		return handleResult(decoder.decode(ops, input)).getFirst();
	}

	public static <T> void encode(Encoder<T> encoder, T data, PacketBuffer buf) {
		INBT nbt = handleResult(encoder.encode(data, NBTDynamicOps.INSTANCE, NBTDynamicOps.INSTANCE.empty()));

		if (nbt instanceof CompoundNBT) {
			buf.writeCompoundTag((CompoundNBT) nbt);
		} else {
			throw new IllegalStateException("Couldn't get a CompoundNBT from the encoder: " + encoder.toString());
		}
	}

	public static <T> JsonElement encode(Encoder<T> encoder, T data) {
		return encode(encoder, JsonOps.INSTANCE, data);
	}

	public static <T, U> U encode(Encoder<T> encoder, DynamicOps<U> ops, T data) {
		return handleResult(encoder.encode(data, ops, ops.empty()));
	}

	/**
	 * A generic way to handle {@link DataResult}
	 * 
	 * @param <T>    the type of data inside the result
	 * @param result the result to handle
	 * @return the data in the result
	 */
	public static <T> T handleResult(DataResult<T> result) {
		return result.resultOrPartial(DataPackAnvilApi.LOGGER::warn)
				.orElseThrow(() -> new IllegalStateException(result.error().map(PartialResult::message).orElse("Error while decoding data, no error message found...")));
	}
}
