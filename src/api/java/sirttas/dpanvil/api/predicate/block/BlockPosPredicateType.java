package sirttas.dpanvil.api.predicate.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.ApiStatus;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.predicate.block.direction.FacingBlockPredicate;
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

public record BlockPosPredicateType<T extends IBlockPosPredicate>(MapCodec<T> codec) {

	public static final ResourceKey<Registry<BlockPosPredicateType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DPAnvilNames.ResourceLocations.create("block_pos_predicate"));
	private static final DeferredRegister<BlockPosPredicateType<?>> DEFERRED_REGISTRY = DeferredRegister.create(REGISTRY_KEY, DataPackAnvilApi.MODID);

	public static final Registry<BlockPosPredicateType<?>> REGISTRY = DEFERRED_REGISTRY.makeRegistry(Consumers.nop());

	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<AnyBlockPredicate>> ANY = register(AnyBlockPredicate.NAME, AnyBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<NoneBlockPredicate>> NONE = register(NoneBlockPredicate.NAME, NoneBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<OrBlockPredicate>> OR = register(OrBlockPredicate.NAME, OrBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<AndBlockPredicate>> AND = register(AndBlockPredicate.NAME, AndBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<NotBlockPredicate>> NOT = register(NotBlockPredicate.NAME, NotBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlockPredicate>> MATCH_BLOCK = register(MatchBlockPredicate.NAME, MatchBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlocksPredicate>> MATCH_BLOCKS = register(MatchBlocksPredicate.NAME, MatchBlocksPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlockTagPredicate>> MATCH_TAG = register(MatchBlockTagPredicate.NAME, MatchBlockTagPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<MatchBlockStatePredicate>> MATCH_STATE = register(MatchBlockStatePredicate.NAME, MatchBlockStatePredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<OffsetBlockPredicate>> OFFSET = register(OffsetBlockPredicate.NAME, OffsetBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<CacheBlockPredicate>> CACHE = register(CacheBlockPredicate.NAME, CacheBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<FacingBlockPredicate>> FACING = register(FacingBlockPredicate.NAME, FacingBlockPredicate.CODEC);

	private static <T extends IBlockPosPredicate> DeferredHolder<BlockPosPredicateType<?>, BlockPosPredicateType<T>> register(String name, MapCodec<T> codec) {
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
