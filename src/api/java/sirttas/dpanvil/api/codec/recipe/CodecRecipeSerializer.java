package sirttas.dpanvil.api.codec.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;

public class CodecRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

	private final Codec<T> codec;

	public CodecRecipeSerializer(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public @NotNull Codec<T> codec() {
		return codec;
	}

	@Override
	public @Nullable T fromNetwork(@NotNull FriendlyByteBuf buffer) {
		return CodecHelper.decode(codec, buffer);
	}

	@Override
	public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
		CodecHelper.encode(codec, recipe, buffer);
	}

}
