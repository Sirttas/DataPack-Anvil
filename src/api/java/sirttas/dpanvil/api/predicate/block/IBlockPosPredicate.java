package sirttas.dpanvil.api.predicate.block;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
import sirttas.dpanvil.api.predicate.block.world.CacheBlockPredicate;
import sirttas.dpanvil.api.predicate.block.world.OffsetBlockPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IBlockPosPredicate {

	Codec<IBlockPosPredicate> CODEC = BlockPosPredicateType.REGISTRY.byNameCodec().dispatch(IBlockPosPredicate::getType, BlockPosPredicateType::codec);


	boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction);

	BlockPosPredicateType<? extends IBlockPosPredicate> getType();

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

    default IBlockPosPredicate offset(Vec3i offset) {
        return new OffsetBlockPredicate(this, offset);
    }

	default IBlockPosPredicate offset(int x, int y, int z) {
		return offset(new BlockPos(x, y, z));
	}

	default IBlockPosPredicate cache() {
		return new CacheBlockPredicate(this);
	}

	static IBlockPosPredicate any() {
		return AnyBlockPredicate.get();
	}

	static IBlockPosPredicate none() {
		return NoneBlockPredicate.get();
	}

	static IBlockPosPredicate createOr(IBlockPosPredicate... predicates) {
		return new OrBlockPredicate(predicates);
	}

	static IBlockPosPredicate createAnd(IBlockPosPredicate... predicates) {
		return new AndBlockPredicate(predicates);
	}

	static IBlockPosPredicate match(Block... blocks) {
		if (blocks.length == 1) {
			return new MatchBlockPredicate(blocks[0]);
		}
		return new MatchBlocksPredicate(blocks);
	}

	static IBlockPosPredicate match(TagKey<Block> tag) {
		return new MatchBlockTagPredicate(tag);
	}

	static IBlockPosPredicate match(BlockState state) {
		return new MatchBlockStatePredicate(state);
	}
	
	default JsonElement write() {
		return CodecHelper.encode(CODEC, this);
	}

	default void write(FriendlyByteBuf buf) {
		CodecHelper.encode(CODEC, this, buf);
	}
	
	static IBlockPosPredicate read(JsonElement json) {
		IBlockPosPredicate value = CodecHelper.decode(CODEC, json);
		
		return value != null ? value.simplify() : null;
	}

	static IBlockPosPredicate read(FriendlyByteBuf buf) {
		return CodecHelper.decode(CODEC, buf);
	}
	
	default IBlockPosPredicate simplify() {
		return this;
	}
}
