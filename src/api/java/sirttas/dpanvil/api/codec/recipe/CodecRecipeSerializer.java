package sirttas.dpanvil.api.codec.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.codec.CodecHelper;

import javax.annotation.Nonnull;

@Deprecated
public class CodecRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<@NotNull T> {

	private final MapCodec<T> codec;
	private final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull T> streamCodec;

	public CodecRecipeSerializer(MapCodec<T> codec) {
		this.codec = codec;
		this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
	}

	@Override
	public @NotNull MapCodec<T> codec() {
		return codec;
	}

	@Override
	public @NotNull StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull T> streamCodec() {
		return streamCodec;
	}

	public T fromNetwork(@NotNull FriendlyByteBuf buffer) {
		return CodecHelper.decode(codec, buffer);
	}

	public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
		CodecHelper.encode(codec, recipe, buffer);
	}

}
