package sirttas.dpanvil.api.predicate.block;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.ICodecProvider;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.AnyBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NoneBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockStatePredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;
import sirttas.dpanvil.api.predicate.block.world.OffsetBlockPredicate;

import java.util.function.Supplier;

public record BlockPosPredicateType<T extends IBlockPosPredicate>(Codec<T> codec) implements ICodecProvider<T> {

	public static final ResourceKey<Registry<BlockPosPredicateType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DataPackAnvilApi.createRL("block_pos_predicate"));
	private static final DeferredRegister<BlockPosPredicateType<?>> DEFERRED_REGISTRY = DeferredRegister.create(REGISTRY_KEY, DataPackAnvilApi.MODID);

	public static final Supplier<IForgeRegistry<BlockPosPredicateType<?>>> REGISTRY = DEFERRED_REGISTRY.makeRegistry(RegistryBuilder::new);

	public static final RegistryObject<BlockPosPredicateType<AnyBlockPredicate>> ANY = register(AnyBlockPredicate.CODEC, AnyBlockPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<NoneBlockPredicate>> NONE = register(NoneBlockPredicate.CODEC, NoneBlockPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<OrBlockPredicate>> OR = register(OrBlockPredicate.CODEC, OrBlockPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<AndBlockPredicate>> AND = register(AndBlockPredicate.CODEC, AndBlockPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<NotBlockPredicate>> NOT = register(NotBlockPredicate.CODEC, NotBlockPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<MatchBlockPredicate>> MATCH_BLOCK = register(MatchBlockPredicate.CODEC, MatchBlockPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<MatchBlocksPredicate>> MATCH_BLOCKS = register(MatchBlocksPredicate.CODEC, MatchBlocksPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<MatchBlockTagPredicate>> MATCH_TAG = register(MatchBlockTagPredicate.CODEC, MatchBlockTagPredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<MatchBlockStatePredicate>> MATCH_STATE = register(MatchBlockStatePredicate.CODEC, MatchBlockStatePredicate.NAME);
	public static final RegistryObject<BlockPosPredicateType<OffsetBlockPredicate>> OFFSET = register(OffsetBlockPredicate.CODEC, OffsetBlockPredicate.NAME);

	private static <T extends IBlockPosPredicate> RegistryObject<BlockPosPredicateType<T>> register(Codec<T> codec, String name) {
		return DEFERRED_REGISTRY.register(name, () -> new BlockPosPredicateType<>(codec));
	}

	/**
	 * For internal use only.
	 */
	@ApiStatus.Internal
	public static void register(IEventBus bus) {
		DEFERRED_REGISTRY.register(bus);
	}
}
