package sirttas.dpanvil.api.codec;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.network.PacketBuffer;
import sirttas.dpanvil.api.data.IJsonDataSerializer;

public class CodecJsonDataSerializer<T> implements IJsonDataSerializer<T> {

	private final Codec<T> codec;
	
	public CodecJsonDataSerializer(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public T read(JsonObject json) {
		return CodecHelper.decode(codec, json);
	}

	@Override
	public T read(PacketBuffer buf) {
		return CodecHelper.decode(codec, buf);
	}

	@Override
	public void write(T data, PacketBuffer buf) {
		CodecHelper.encode(codec, data, buf);
	}
}
