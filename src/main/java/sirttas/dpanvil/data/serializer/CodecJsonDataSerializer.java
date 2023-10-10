package sirttas.dpanvil.data.serializer;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.codec.CodecHelper;

public record CodecJsonDataSerializer<T>(
		Codec<T> codec
) implements IJsonDataSerializer<T> {

	@Override
	public T read(JsonElement json) {
		return CodecHelper.decode(codec, DataPackAnvil.WRAPPER.getRegistryOps(JsonOps.INSTANCE), json);
	}

	@Override
	public T read(FriendlyByteBuf buf) {
		return CodecHelper.decode(codec, DataPackAnvil.WRAPPER.getRegistryOps(NbtOps.INSTANCE), buf.readNbt());
	}

	@Override
	public void write(T data, FriendlyByteBuf buf) {
		CodecHelper.encode(codec, DataPackAnvil.WRAPPER.getRegistryOps(NbtOps.INSTANCE), data, buf);
	}
}
