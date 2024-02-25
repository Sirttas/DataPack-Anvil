package sirttas.dpanvil.api.predicate.block.direction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

public enum FacingBlockPredicate implements IBlockPosPredicate {

    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    public static final String NAME = "facing";

    public static final Codec<FacingBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Direction.CODEC.fieldOf("direction").forGetter(f -> f.direction)
    ).apply(builder, d -> switch (d) {
        case DOWN -> DOWN;
        case UP -> UP;
        case NORTH -> NORTH;
        case SOUTH -> SOUTH;
        case WEST -> WEST;
        case EAST -> EAST;
    }));

    private final Direction direction;

    FacingBlockPredicate(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean test(@NotNull LevelReader level, @NotNull BlockPos pos, @Nullable Direction direction) {
        return direction == this.direction;
    }

    @Override
    public BlockPosPredicateType<? extends IBlockPosPredicate> getType() {
        return BlockPosPredicateType.FACING.get();
    }
}
