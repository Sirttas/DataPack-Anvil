package sirttas.dpanvil.api.predicate.block;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.function.Consumers;
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
import sirttas.dpanvil.api.predicate.block.world.CacheBlockPredicate;
import sirttas.dpanvil.api.predicate.block.world.OffsetBlockPredicate;

public record BlockPosPredicateType<T extends IBlockPosPredicate>(Codec<T> codec) implements ICodecProvider<T> {

	public static final ResourceKey<Registry<BlockPosPredicateType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DataPackAnvilApi.createRL("block_pos_predicate"));
	private static final DeferredRegister<BlockPosPredicateType<?>> DEFERRED_REGISTRY = DeferredRegister.create(REGISTRY_KEY, DataPackAnvilApi.MODID);

	public static final Registry<BlockPosPredicateType<?>> REGISTRY = DEFERRED_REGISTRY.makeRegistry(Consumers.nop());

	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<AnyBlockPredicate>> ANY = register(AnyBlockPredicate.CODEC, AnyBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<NoneBlockPredicate>> NONE = register(NoneBlockPredicate.CODEC, NoneBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<OrBlockPredicate>> OR = register(OrBlockPredicate.CODEC, OrBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<AndBlockPredicate>> AND = register(AndBlockPredicate.CODEC, AndBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<NotBlockPredicate>> NOT = register(NotBlockPredicate.CODEC, NotBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlockPredicate>> MATCH_BLOCK = register(MatchBlockPredicate.CODEC, MatchBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlocksPredicate>> MATCH_BLOCKS = register(MatchBlocksPredicate.CODEC, MatchBlocksPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlockTagPredicate>> MATCH_TAG = register(MatchBlockTagPredicate.CODEC, MatchBlockTagPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlockStatePredicate>> MATCH_STATE = register(MatchBlockStatePredicate.CODEC, MatchBlockStatePredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<OffsetBlockPredicate>> OFFSET = register(OffsetBlockPredicate.CODEC, OffsetBlockPredicate.NAME);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<CacheBlockPredicate>> CACHE = register(CacheBlockPredicate.CODEC, CacheBlockPredicate.NAME);

	private static <T extends IBlockPosPredicate> DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<T>> register(Codec<T> codec, String name) {
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
