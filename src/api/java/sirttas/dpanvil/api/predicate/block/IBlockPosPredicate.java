package sirttas.dpanvil.api.predicate.block;

import java.util.List;
import java.util.function.BiPredicate;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import sirttas.dpanvil.api.codec.CodecHelper;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.AnyBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NoneBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.NotBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockStatePredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlockTagPredicate;
import sirttas.dpanvil.api.predicate.block.match.MatchBlocksPredicate;

public interface IBlockPosPredicate {

	public static final Codec<IBlockPosPredicate> CODEC = CodecHelper.getRegistryCodec(() -> BlockPosPredicateType.REGISTRY).dispatch(IBlockPosPredicate::getType, BlockPosPredicateType::getCodec);
	
	boolean test(IWorldReader world, BlockPos pos);

	BlockPosPredicateType<? extends IBlockPosPredicate> getType();

	default BiPredicate<IWorldReader, BlockPos> asBlockPosPredicate() {
		return this::test;
	}

	default IBlockPosPredicate or(IBlockPosPredicate... predicates) {
		List<IBlockPosPredicate> list = Lists.newArrayList(predicates);

		list.add(this);
		return new OrBlockPredicate(list);
	}

	default IBlockPosPredicate and(IBlockPosPredicate... predicates) {
		List<IBlockPosPredicate> list = Lists.newArrayList(predicates);
		
		list.add(this);
		return new AndBlockPredicate(list);
	}

	default IBlockPosPredicate not() {
		return new NotBlockPredicate(this);
	}

	public static IBlockPosPredicate any() {
		return AnyBlockPredicate.INSTANCE;
	}

	public static IBlockPosPredicate none() {
		return NoneBlockPredicate.INSTANCE;
	}

	public static IBlockPosPredicate createOr(IBlockPosPredicate... predicates) {
		return new OrBlockPredicate(predicates);
	}

	public static IBlockPosPredicate createAnd(IBlockPosPredicate... predicates) {
		return new AndBlockPredicate(predicates);
	}

	public static IBlockPosPredicate match(Block... blocks) {
		if (blocks.length == 1) {
			return new MatchBlockPredicate(blocks[0]);
		}
		return new MatchBlocksPredicate(blocks);
	}

	public static IBlockPosPredicate match(INamedTag<Block> tag) {
		return new MatchBlockTagPredicate(tag);
	}

	public static IBlockPosPredicate match(BlockState state) {
		return new MatchBlockStatePredicate(state);
	}
	
	default JsonElement write() {
		return CodecHelper.encode(CODEC, this);
	}

	default void write(PacketBuffer buf) {
		CodecHelper.encode(CODEC, this, buf);
	}
	
	public static IBlockPosPredicate read(JsonElement json) {
		return CodecHelper.decode(CODEC, json);
	}

	public static IBlockPosPredicate read(PacketBuffer buf) {
		return CodecHelper.decode(CODEC, buf);
	}
}
