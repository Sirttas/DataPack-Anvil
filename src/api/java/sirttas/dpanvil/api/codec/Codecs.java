package sirttas.dpanvil.api.codec;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicates;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

@SuppressWarnings("deprecation")
public class Codecs {

	public static final Codec<Block> BLOCK = Registry.BLOCK;
	public static final Codec<Item> ITEM = Registry.ITEM;
	/**
	 * @deprecated use {@link BlockPosPredicates.CODEC}
	 */
	@Deprecated
	public static final Codec<IBlockPosPredicate> BLOCK_PREDICATE = BlockPosPredicates.CODEC;

	public static final Codec<Integer> HEX_COLOR = Codec.STRING.comapFlatMap(s -> {
		if (s.startsWith("#")) {
			return DataResult.success(Integer.parseInt(s.substring(1), 16));
		}
		return DataResult.error("Coundn't parse color: '" + s + '\'');
	}, i -> String.format("#%06X", i));
	public static final Codec<Integer> RGB_COLOR = RecordCodecBuilder.create(builder -> builder.group(
			Codec.INT.fieldOf("r").forGetter(i -> (i >> 16) & 0xFF),
			Codec.INT.fieldOf("g").forGetter(i -> (i >> 8) & 0xFF),
			Codec.INT.fieldOf("b").forGetter(i -> i & 0xFF)
	).apply(builder, (r, g, b) -> ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff)));
	public static final Codec<Integer> COLOR = Codec.either(Codec.INT, Codec.either(HEX_COLOR, RGB_COLOR).xmap(e -> e.map(Function.identity(), Function.identity()), Either::left))
			.xmap(e -> e.map(Function.identity(), Function.identity()), Either::left);

}
