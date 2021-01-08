package sirttas.dpanvil.data.serializer;

import com.google.gson.JsonElement;

import net.minecraft.network.PacketBuffer;

public interface IJsonDataSerializer<T> {

	T read(JsonElement jsonObject);
	T read(PacketBuffer buf);
	void write(T data, PacketBuffer buf);
}
