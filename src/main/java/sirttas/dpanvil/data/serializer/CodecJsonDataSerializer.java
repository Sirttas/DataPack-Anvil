package sirttas.dpanvil.data.serializer;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import sirttas.dpanvil.api.codec.CodecHelper;
import sirttas.dpanvil.registry.RegistryListener;

public record CodecJsonDataSerializer<T>(
		Codec<T> codec
) implements IJsonDataSerializer<T, Tag> {

	@Override
	public T read(JsonElement json) {
		return CodecHelper.decode(codec, RegistryListener.getInstance().getRegistryOps(JsonOps.INSTANCE), json);
	}

	@Override
	public Tag read(FriendlyByteBuf buf) {
		return buf.readNbt();
	}

	@Override
	public T read(Tag nbt) {
		return CodecHelper.decode(codec, RegistryListener.getInstance().getRegistryOps(NbtOps.INSTANCE), nbt);
	}

	@Override
	public void write(T data, FriendlyByteBuf buf) {
		CodecHelper.encode(codec, RegistryListener.getInstance().getRegistryOps(NbtOps.INSTANCE), data, buf);
	}
}
