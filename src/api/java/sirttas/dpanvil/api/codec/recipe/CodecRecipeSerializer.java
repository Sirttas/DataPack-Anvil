package sirttas.dpanvil.api.codec.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class CodecRecipeSerializer<T extends Recipe<?>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

	private final Codec<T> codec;
	private final BiConsumer<T, ResourceLocation> idSetter;

	public CodecRecipeSerializer(Codec<T> codec, BiConsumer<T, ResourceLocation> idSetter) {
		this.codec = codec;
		this.idSetter = idSetter;
	}
	
	@Override
	public @Nonnull T fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
		T recipe = CodecHelper.decode(codec, json);

		assert recipe != null;
		idSetter.accept(recipe, recipeId);
		return recipe;
	}

	@Override
	public @Nonnull T fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
		T recipe = CodecHelper.decode(codec, buffer);

		assert recipe != null;
		idSetter.accept(recipe, recipeId);
		return recipe;
	}

	@Override
	public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
		CodecHelper.encode(codec, recipe, buffer);
	}

}
