package sirttas.dpanvil.api.codec.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.codec.CodecHelper;

import java.util.function.BiConsumer;

public class CodecRecipeSerializer<T extends Recipe<?>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

	private final Codec<T> codec;
	private final BiConsumer<T, ResourceLocation> idSetter;

	public CodecRecipeSerializer(Codec<T> codec, BiConsumer<T, ResourceLocation> idSetter) {
		this.codec = codec;
		this.idSetter = idSetter;
	}
	
	@Override
	public @NotNull T fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
		T recipe = CodecHelper.decode(codec, json);

		assert recipe != null;
		idSetter.accept(recipe, recipeId);
		return recipe;
	}

	@Override
	public @NotNull T fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
		T recipe = CodecHelper.decode(codec, buffer);

		assert recipe != null;
		idSetter.accept(recipe, recipeId);
		return recipe;
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull T recipe) {
		CodecHelper.encode(codec, recipe, buffer);
	}

}
