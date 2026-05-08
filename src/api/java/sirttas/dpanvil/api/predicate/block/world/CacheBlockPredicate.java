package sirttas.dpanvil.api.predicate.block.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public record CacheBlockPredicate(
        IBlockPosPredicate predicate
) implements IBlockPosPredicate {

    public static final String NAME = "cache";
    public static final MapCodec<CacheBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(CacheBlockPredicate::predicate)
    ).apply(builder, CacheBlockPredicate::new));

    @Override
    public boolean test(LevelReader level, BlockPos pos, @Nullable Direction direction) {
        return predicate.test(switch (level) {
            case CacheLevel cacheLevel -> cacheLevel;
            case ServerLevelAccessor serverLevelAccessor -> new CacheLevel(serverLevelAccessor);
            default -> level;
        }, pos, direction);
    }

    @Override
    public BlockPosPredicateType<CacheBlockPredicate> getType() {
        return BlockPosPredicateType.CACHE.get();
    }

    @Override
    public List<Component> getTooltip() {
        return predicate.getTooltip();
    }

    private static class CacheLevel implements ServerLevelAccessor {

        private final ServerLevel level;
        private final Map<BlockPos, BlockEntity> blockEntityCache;

        public CacheLevel(ServerLevelAccessor level) {
            this.level = level.getLevel();
            this.blockEntityCache = new HashMap<>();
        }

        @Override
        public ServerLevel getLevel() {
            return level;
        }

        @Override
        public long nextSubTickCount() {
            return level.nextSubTickCount();
        }

        @Override
        public LevelTickAccess<Block> getBlockTicks() {
            return level.getBlockTicks();
        }

        @Override
        public LevelTickAccess<Fluid> getFluidTicks() {
            return level.getFluidTicks();
        }

        @Override
        public LevelData getLevelData() {
            return level.getLevelData();
        }

        @Override
        public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
            return level.getCurrentDifficultyAt(pos);
        }

        @Nullable
        @Override
        public MinecraftServer getServer() {
            return level.getServer();
        }

        @Override
        public ChunkSource getChunkSource() {
            return level.getChunkSource();
        }

        @Override
        public RandomSource getRandom() {
            return level.getRandom();
        }

        @Override
        public void playSound(@Nullable Entity entity, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float v, float v1) {
            level.playSound(entity, blockPos, soundEvent, soundSource, v, v1);
        }

        @Override
        public void addParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            level.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        @Override
        public void levelEvent(@Nullable Entity entity, int i, BlockPos blockPos, int i1) {
            level.levelEvent(entity, i, blockPos, i1);
        }

        @Override
        public void gameEvent(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context) {
            level.gameEvent(holder, vec3, context);
        }

        @Override
        public RegistryAccess registryAccess() {
            return level.registryAccess();
        }

        @Override
        public FeatureFlagSet enabledFeatures() {
            return level.enabledFeatures();
        }

        @Override
        public EnvironmentAttributeReader environmentAttributes() {
            return level.environmentAttributes();
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return level.getLightEngine();
        }

        @Override
        public WorldBorder getWorldBorder() {
            return level.getWorldBorder();
        }

        @Nullable
        @Override
        public synchronized BlockEntity getBlockEntity(BlockPos pos) {
            return blockEntityCache.computeIfAbsent(pos, level::getBlockEntity);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return level.getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return level.getFluidState(pos);
        }

        @Override
        public List<Entity> getEntities(@Nullable Entity entity, AABB area, Predicate<? super Entity> predicate) {
            return level.getEntities(entity, area, predicate);
        }

        @Override
        public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate) {
            return level.getEntities(entityTypeTest, area, predicate);
        }

        @Override
        public List<? extends Player> players() {
            return level.players();
        }

        @Nullable
        @Override
        public ChunkAccess getChunk(int i, int i1, ChunkStatus chunkStatus, boolean b) {
            return level.getChunk(i, i1, chunkStatus, b);
        }

        @Override
        public int getHeight(Heightmap.Types heightmapType, int pX, int pZ) {
            return level.getHeight(heightmapType, pX, pZ);
        }

        @Override
        public int getSkyDarken() {
            return level.getSkyDarken();
        }

        @Override
        public BiomeManager getBiomeManager() {
            return level.getBiomeManager();
        }

        @Override
        public Holder<Biome> getUncachedNoiseBiome(int pX, int pY, int pZ) {
            return level.getUncachedNoiseBiome(pX, pY, pZ);
        }

        @Override
        public boolean isClientSide() {
            return level.isClientSide();
        }

        @Override
        public int getSeaLevel() {
            return level.getSeaLevel();
        }

        @Override
        public DimensionType dimensionType() {
            return level.dimensionType();
        }

        @Override
        public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> state) {
            return level.isStateAtPosition(pos, state);
        }

        @Override
        public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> predicate) {
            return level.isFluidAtPosition(pos, predicate);
        }

        @Override
        public boolean setBlock(BlockPos pos, BlockState state, int pFlags, int pRecursionLeft) {
            return level.setBlock(pos, state, pFlags, pRecursionLeft);
        }

        @Override
        public boolean removeBlock(BlockPos pos, boolean pIsMoving) {
            return level.removeBlock(pos, pIsMoving);
        }

        @Override
        public boolean destroyBlock(BlockPos pos, boolean pDropBlock, @Nullable Entity pEntity, int pRecursionLeft) {
            return level.destroyBlock(pos, pDropBlock, pEntity, pRecursionLeft);
        }
    }
}
