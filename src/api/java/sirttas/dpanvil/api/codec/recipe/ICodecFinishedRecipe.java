package sirttas.dpanvil.api.codec.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.data.IFinishedRecipe;
import sirttas.dpanvil.api.codec.CodecHelper;

public interface ICodecFinishedRecipe<T extends ICodecFinishedRecipe<T>> extends IFinishedRecipe {

	Codec<T> getCodec();

	@SuppressWarnings("unchecked")
	@Override
	default void serialize(JsonObject json) {
		((JsonObject) CodecHelper.encode(getCodec(), (T) this)).entrySet().forEach(e -> json.add(e.getKey(), e.getValue()));
	}
}
