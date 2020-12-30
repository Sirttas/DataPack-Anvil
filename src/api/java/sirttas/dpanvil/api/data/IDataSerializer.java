package sirttas.dpanvil.api.data;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;

public interface IDataSerializer<T> extends JsonDeserializer<T> {

	T read(JsonObject json);
	T read(PacketBuffer buf);
	void write(T data, PacketBuffer buf);

	@Override
	default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		return read((JsonObject) json);
	}
}
