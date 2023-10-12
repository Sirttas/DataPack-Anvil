package sirttas.dpanvil.data.serializer;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;

public interface IJsonDataSerializer<T, I> {

	T read(JsonElement jsonObject);
	I read(FriendlyByteBuf buf);
	T read(I data);
	void write(T data, FriendlyByteBuf buf);
}
