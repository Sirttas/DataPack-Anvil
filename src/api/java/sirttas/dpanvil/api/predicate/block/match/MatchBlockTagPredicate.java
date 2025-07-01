package sirttas.dpanvil.api.predicate.block.match;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockStatePredicate;

import javax.annotation.Nonnull;
import java.util.List;

public record MatchBlockTagPredicate(
		TagKey<Block> tag
) implements IBlockStatePredicate {

	public static final String NAME = "tag";
	public static final MapCodec<MatchBlockTagPredicate> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
			TagKey.codec(Registries.BLOCK).fieldOf(DPAnvilNames.TAG).forGetter(MatchBlockTagPredicate::tag)
	).apply(builder, MatchBlockTagPredicate::new));

	public MatchBlockTagPredicate(ResourceLocation tagName) {
		this(TagKey.create(Registries.BLOCK, tagName));
	}

	@Override
	public boolean test(BlockState state) {
		return state.is(this.tag);
	}

	@Override
	public BlockPosPredicateType<MatchBlockTagPredicate> getType() {
		return BlockPosPredicateType.MATCH_TAG.get();
	}

	@Override
	@Nonnull
	public List<Component> getTooltip() {
		return List.of(Component.translatable("tooltip.dpanvil.predicate.tag", Component.literal(tag.location().toString())));
	}
}
