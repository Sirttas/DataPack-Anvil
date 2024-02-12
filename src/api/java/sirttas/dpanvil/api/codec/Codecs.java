package sirttas.dpanvil.api.codec;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation"})
public class Codecs {

	public static final Codec<Pattern> PATTERN = Codec.STRING.xmap(Pattern::compile, Pattern::pattern);
	public static final Codec<EquipmentSlot> EQUIPMENT_SLOT_TYPE = Codec.STRING.xmap(EquipmentSlot::byName, EquipmentSlot::getName);

	/**
	 * A {@link Codec} that can read a color from an hex color.
	 */
	public static final Codec<Integer> HEX_COLOR = Codec.STRING.comapFlatMap(s -> {
		if (s.startsWith("#")) {
			return DataResult.success(Integer.parseInt(s.substring(1), 16));
		}
		return DataResult.error(() -> "Couldn't parse color: '" + s + '\'');
	}, i -> String.format("#%06X", i));
	
	/**
	 * A {@link Codec} that can read a color from an rgb map.
	 */
	public static final Codec<Integer> RGB_COLOR = RecordCodecBuilder.create(builder -> builder.group(
			Codec.INT.fieldOf("r").forGetter(i -> (i >> 16) & 0xFF),
			Codec.INT.fieldOf("g").forGetter(i -> (i >> 8) & 0xFF),
			Codec.INT.fieldOf("b").forGetter(i -> i & 0xFF)
	).apply(builder, (r, g, b) -> ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff)));
	
	/**
	 * A {@link Codec} that can read a color either from an int, an hex color string or an rgb map.
	 */
	public static final Codec<Integer> COLOR = Codec.either(Codec.INT, Codec.either(HEX_COLOR, RGB_COLOR)
			.xmap(e -> e.map(Function.identity(), Function.identity()), Either::left))
			.xmap(e -> e.map(Function.identity(), Function.identity()), Either::left);
	
	public static final Codec<Multimap<Attribute, AttributeModifier>> ATTRIBUTE_MULTIMAP = CodecHelper.multiMapCodec(BuiltInRegistries.ATTRIBUTE.byNameCodec(), AttributeModifier.CODEC);

	public static final Codec<AABB> AABB = Codec.DOUBLE.listOf().comapFlatMap(
			list -> Util.fixedSize(list, 6).map(doubles -> new AABB(doubles.get(0), doubles.get(1), doubles.get(2), doubles.get(3), doubles.get(4), doubles.get(5))),
			aabb -> List.of(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
	);

	public static <T> Codec<ResourceKey<T>> keyCodec(ResourceKey<Registry<T>> registryKey) {
		return ResourceLocation.CODEC.xmap(l -> ResourceKey.create(registryKey, l), ResourceKey::location);
	}

	private Codecs() {}

}
