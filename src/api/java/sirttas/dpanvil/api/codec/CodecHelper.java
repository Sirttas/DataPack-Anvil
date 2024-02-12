package sirttas.dpanvil.api.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
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
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CodecHelper {

	private static final Map<DynamicOps<?>, RegistryOps<?>> REGISTRY_OPS = new Reference2ObjectOpenHashMap<>();

	private CodecHelper() {}

	public static synchronized <T> RegistryOps<T> getRegistryOps(DynamicOps<T> ops) {
		return (RegistryOps<T>) REGISTRY_OPS.computeIfAbsent(ops, o -> RegistryOps.create(o, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)));
	}


	public static <K, V> Codec<Multimap<K, V>> multiMapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
		return Codec.unboundedMap(keyCodec, valueCodec.listOf()).xmap(map -> {
			if (map != null) {
				Multimap<K, V> multiMap = HashMultimap.create();
				
				map.forEach(multiMap::putAll);
				return multiMap;
			}
			return null;
		}, multiMap -> multiMap != null ? multiMap.asMap().entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> Lists.newArrayList(e.getValue()))) : null);
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
		return new Encoder<>() {
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
		return new Decoder<>() {
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
		return decode(decoder, getRegistryOps(JsonOps.INSTANCE), json);
	}

	public static <T> T decode(Decoder<T> decoder, FriendlyByteBuf buf) {
		return decode(decoder, buf.readNbt());
	}

	public static <T> T decode(Decoder<T> decoder, Tag nbt) {
		return decode(decoder, getRegistryOps(NbtOps.INSTANCE), nbt);
	}

	public static <T, U> T decode(Decoder<T> decoder, DynamicOps<U> ops, U input) {
		return handleResult(decoder.decode(ops, input)).getFirst();
	}

	public static <T> void encode(Encoder<T> encoder, T data, FriendlyByteBuf buf) {
		encode(encoder, getRegistryOps(NbtOps.INSTANCE), data, buf);
	}
	public static <T> void encode(Encoder<T> encoder, DynamicOps<Tag> ops, T data, FriendlyByteBuf buf) {
		Tag nbt = handleResult(encoder.encode(data, ops, ops.empty()));

		if (nbt instanceof CompoundTag compoundTag) {
			buf.writeNbt(compoundTag);
		} else {
			throw new IllegalStateException("Couldn't get a CompoundNBT from the encoder: " + encoder);
		}
	}

	public static <T> JsonElement encode(Encoder<T> encoder, T data) {
		return encode(encoder, getRegistryOps(JsonOps.INSTANCE), data);
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
