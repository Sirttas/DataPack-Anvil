package sirttas.dpanvil.data.serializer;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import sirttas.dpanvil.api.codec.CodecHelper;

public class CodecJsonDataSerializer<T> implements IJsonDataSerializer<T> {

	private final Codec<T> codec;
	
	public CodecJsonDataSerializer(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public T read(JsonElement json) {
		return CodecHelper.decode(codec, json);
	}

	@Override
	public T read(FriendlyByteBuf buf) {
		return CodecHelper.decode(codec, buf);
	}

	@Override
	public void write(T data, FriendlyByteBuf buf) {
		CodecHelper.encode(codec, data, buf);
	}
}
