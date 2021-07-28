package sirttas.dpanvil.data.serializer;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public interface IJsonDataSerializer<T> {

	T read(JsonElement jsonObject);
	T read(FriendlyByteBuf buf);
	void write(T data, FriendlyByteBuf buf);
}
