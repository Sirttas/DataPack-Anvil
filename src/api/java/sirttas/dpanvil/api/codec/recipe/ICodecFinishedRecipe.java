package sirttas.dpanvil.api.codec.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.data.recipes.FinishedRecipe;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;

public interface ICodecFinishedRecipe<T extends ICodecFinishedRecipe<T>> extends FinishedRecipe {

	Codec<T> getCodec();

	@SuppressWarnings("unchecked")
	@Override
	default void serializeRecipeData(@Nonnull JsonObject json) {
		((JsonObject) CodecHelper.encode(getCodec(), (T) this)).entrySet().forEach(e -> json.add(e.getKey(), e.getValue()));
	}
}
