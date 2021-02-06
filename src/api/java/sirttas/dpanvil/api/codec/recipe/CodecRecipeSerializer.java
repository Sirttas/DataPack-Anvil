package sirttas.dpanvil.api.codec.recipe;

import java.util.function.BiConsumer;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sirttas.dpanvil.api.codec.CodecHelper;

public class CodecRecipeSerializer<T extends IRecipe<?>> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

	private final Codec<T> codec;
	private final BiConsumer<T, ResourceLocation> idSetter;

	public CodecRecipeSerializer(Codec<T> codec, BiConsumer<T, ResourceLocation> idSetter) {
		this.codec = codec;
		this.idSetter = idSetter;
	}
	
	@Override
	public T read(ResourceLocation recipeId, JsonObject json) {
		T recipe = CodecHelper.decode(codec, json);
		
		if (recipe != null) {
			idSetter.accept(recipe, recipeId);
		}
		return recipe;
	}

	@Override
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		T recipe = CodecHelper.decode(codec, buffer);
		
		if (recipe != null) {
			idSetter.accept(recipe, recipeId);
		}
		return recipe;
	}

	@Override
	public void write(PacketBuffer buffer, T recipe) {
		CodecHelper.encode(codec, recipe, buffer);
	}

}
