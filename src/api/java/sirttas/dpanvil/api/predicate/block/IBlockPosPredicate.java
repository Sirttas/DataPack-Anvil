package sirttas.dpanvil.api.predicate.block;

import java.util.List;
import java.util.function.BiPredicate;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag.Named;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
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
	
	boolean test(LevelReader world, BlockPos pos);

	BlockPosPredicateType<? extends IBlockPosPredicate> getType();

	default BiPredicate<LevelReader, BlockPos> asBlockPosPredicate() {
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
		return AnyBlockPredicate.get();
	}

	public static IBlockPosPredicate none() {
		return NoneBlockPredicate.get();
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

	public static IBlockPosPredicate match(Named<Block> tag) {
		return new MatchBlockTagPredicate(tag);
	}

	public static IBlockPosPredicate match(BlockState state) {
		return new MatchBlockStatePredicate(state);
	}
	
	default JsonElement write() {
		return CodecHelper.encode(CODEC, this);
	}

	default void write(FriendlyByteBuf buf) {
		CodecHelper.encode(CODEC, this, buf);
	}
	
	public static IBlockPosPredicate read(JsonElement json) {
		IBlockPosPredicate value = CodecHelper.decode(CODEC, json);
		
		return value != null ? value.simplify() : null;
	}

	public static IBlockPosPredicate read(FriendlyByteBuf buf) {
		return CodecHelper.decode(CODEC, buf);
	}
	
	default IBlockPosPredicate simplify() {
		return this;
	}
}
