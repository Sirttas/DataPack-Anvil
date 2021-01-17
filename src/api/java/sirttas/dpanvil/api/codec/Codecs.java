package sirttas.dpanvil.api.codec;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicates;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

@SuppressWarnings("deprecation")
public class Codecs {

	public static final Codec<Block> BLOCK = Registry.BLOCK;
	public static final Codec<Item> ITEM = Registry.ITEM;
	public static final Codec<IBlockPosPredicate> BLOCK_PREDICATE = BlockPosPredicates.CODEC;

}
