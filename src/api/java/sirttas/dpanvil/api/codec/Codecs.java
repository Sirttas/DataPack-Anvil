package sirttas.dpanvil.api.codec;

import java.util.UUID;
import java.util.function.Function;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import sirttas.dpanvil.api.DPAnvilNames;

@SuppressWarnings("deprecation")
public class Codecs {

	public static final Codec<Block> BLOCK = Registry.BLOCK;
	public static final Codec<Item> ITEM = Registry.ITEM;
	public static final Codec<Enchantment> ENCHANTMENT = Registry.ENCHANTMENT;
	public static final Codec<Attribute> ATTRIBUTE = Registry.ATTRIBUTE;
	public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
	public static final Codec<EquipmentSlot> EQUIPMENT_SLOT_TYPE = Codec.STRING.xmap(EquipmentSlot::byName, EquipmentSlot::getName);

	/**
	 * A {@link Codec} that can read a color from an hex color.
	 */
	public static final Codec<Integer> HEX_COLOR = Codec.STRING.comapFlatMap(s -> {
		if (s.startsWith("#")) {
			return DataResult.success(Integer.parseInt(s.substring(1), 16));
		}
		return DataResult.error("Coundn't parse color: '" + s + '\'');
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
	
	private static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION = Codec.STRING.comapFlatMap(s -> {
		switch (s) {
		case "addition":
			return DataResult.success(AttributeModifier.Operation.ADDITION);
		case "multiply_base":
			return DataResult.success(AttributeModifier.Operation.MULTIPLY_BASE);
		case "multiply_total":
			return DataResult.success(AttributeModifier.Operation.MULTIPLY_TOTAL);
		default:
			return DataResult.error("Coundn't parse AttributeModifier Operation: '" + s + '\'');
		}
	}, o -> {
		switch (o) {
		case MULTIPLY_BASE:
			return "multiply_base";
		case MULTIPLY_TOTAL:
			return "multiply_total";
		default:
			return "addition";
		}
	});
	
	public static final Codec<AttributeModifier> ATTRIBUTE_MODIFIER = RecordCodecBuilder.create(builder -> builder.group(
			Codec.STRING.fieldOf(DPAnvilNames.NAME).forGetter(AttributeModifier::getName),
			Codec.DOUBLE.fieldOf(DPAnvilNames.AMOUNT).forGetter(AttributeModifier::getAmount),
			ATTRIBUTE_MODIFIER_OPERATION.fieldOf(DPAnvilNames.OPERATION).forGetter(AttributeModifier::getOperation)
	).apply(builder, AttributeModifier::new));
	
	public static final Codec<Multimap<Attribute, AttributeModifier>> ATTRIBUTE_MULTIMAP = CodecHelper.multimapCodec(ATTRIBUTE, ATTRIBUTE_MODIFIER);
	
	private Codecs() {}
}
