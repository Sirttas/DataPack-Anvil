package sirttas.dpanvil.api.predicate.block;

import com.mojang.serialization.Codec;

import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import sirttas.dpanvil.api.codec.ICodecProvider;

public class BlockPosPredicateType<T extends IBlockPosPredicate> extends ForgeRegistryEntry<BlockPosPredicateType<?>> implements ICodecProvider<T> {

	public static final IForgeRegistry<BlockPosPredicateType<?>> REGISTRY = RegistryManager.ACTIVE.getRegistry(BlockPosPredicateType.class);

	private final Codec<T> codec;

	public BlockPosPredicateType(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<T> getCodec() {
		return codec;
	}
}
