package sirttas.dpanvil.api.codec.recipe;

import java.util.function.BiConsumer;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sirttas.dpanvil.api.codec.CodecHelper;

public class CodecRecipeSerializer<T extends Recipe<?>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

	private final Codec<T> codec;
	private final BiConsumer<T, ResourceLocation> idSetter;

	public CodecRecipeSerializer(Codec<T> codec, BiConsumer<T, ResourceLocation> idSetter) {
		this.codec = codec;
		this.idSetter = idSetter;
	}
	
	@Override
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		T recipe = CodecHelper.decode(codec, json);
		
		if (recipe != null) {
			idSetter.accept(recipe, recipeId);
		}
		return recipe;
	}

	@Override
	public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		T recipe = CodecHelper.decode(codec, buffer);
		
		if (recipe != null) {
			idSetter.accept(recipe, recipeId);
		}
		return recipe;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, T recipe) {
		CodecHelper.encode(codec, recipe, buffer);
	}

}
