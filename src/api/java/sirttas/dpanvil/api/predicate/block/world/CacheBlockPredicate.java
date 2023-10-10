package sirttas.dpanvil.api.predicate.block.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.level.chunk.ChunkStatus;
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
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.api.predicate.block.IBlockPosPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public record CacheBlockPredicate(
        IBlockPosPredicate predicate
) implements IBlockPosPredicate {

    public static final String NAME = "cache";
    public static final Codec<CacheBlockPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            IBlockPosPredicate.CODEC.fieldOf(DPAnvilNames.VALUE).forGetter(CacheBlockPredicate::predicate)
    ).apply(builder, CacheBlockPredicate::new));

    @Override
    public boolean test(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nullable Direction direction) {
        return predicate.test(level instanceof ServerLevelAccessor accessor ? new CacheLevel(accessor) : level, pos, direction);
    }

    @Override
    public BlockPosPredicateType<CacheBlockPredicate> getType() {
        return BlockPosPredicateType.CACHE.get();
    }

    private static class CacheLevel implements ServerLevelAccessor {

        private final ServerLevel level;
        private final Map<BlockPos, BlockEntity> blockEntityCache;

        public CacheLevel(ServerLevelAccessor level) {
            this.level = level.getLevel();
            this.blockEntityCache = new HashMap<>();
        }

        @Nonnull
        @Override
        public ServerLevel getLevel() {
            return level;
        }

        @Override
        public long nextSubTickCount() {
            return level.nextSubTickCount();
        }

        @Nonnull
        @Override
        public LevelTickAccess<Block> getBlockTicks() {
            return level.getBlockTicks();
        }

        @Nonnull
        @Override
        public LevelTickAccess<Fluid> getFluidTicks() {
            return level.getFluidTicks();
        }

        @Nonnull
        @Override
        public LevelData getLevelData() {
            return level.getLevelData();
        }

        @Nonnull
        @Override
        public DifficultyInstance getCurrentDifficultyAt(@Nonnull BlockPos pos) {
            return level.getCurrentDifficultyAt(pos);
        }

        @Nullable
        @Override
        public MinecraftServer getServer() {
            return level.getServer();
        }

        @Nonnull
        @Override
        public ChunkSource getChunkSource() {
            return level.getChunkSource();
        }

        @Nonnull
        @Override
        public RandomSource getRandom() {
            return level.getRandom();
        }

        @Override
        public void playSound(@Nullable Player player, @Nonnull BlockPos pos, @Nonnull SoundEvent sound, @Nonnull SoundSource category, float volume, float pitch) {
            level.playSound(player, pos, sound, category, volume, pitch);
        }

        @Override
        public void addParticle(@Nonnull ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            level.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        @Override
        public void levelEvent(@Nullable Player player, int type, @Nonnull BlockPos pos, int data) {
            level.levelEvent(player, type, pos, data);
        }

        @Override
        public void gameEvent(@Nonnull GameEvent event, @Nonnull Vec3 position, @Nonnull GameEvent.Context context) {
            level.gameEvent(event, position, context);
        }

        @Nonnull
        @Override
        public RegistryAccess registryAccess() {
            return level.registryAccess();
        }

        @Nonnull
        @Override
        public FeatureFlagSet enabledFeatures() {
            return level.enabledFeatures();
        }

        @Override
        public float getShade(@Nonnull Direction direction, boolean pShade) {
            return level.getShade(direction, pShade);
        }

        @Nonnull
        @Override
        public LevelLightEngine getLightEngine() {
            return level.getLightEngine();
        }

        @Nonnull
        @Override
        public WorldBorder getWorldBorder() {
            return level.getWorldBorder();
        }

        @Nullable
        @Override
        public synchronized BlockEntity getBlockEntity(@Nonnull BlockPos pos) {
            return blockEntityCache.computeIfAbsent(pos, level::getBlockEntity);
        }

        @Nonnull
        @Override
        public BlockState getBlockState(@Nonnull BlockPos pos) {
            return level.getBlockState(pos);
        }

        @Nonnull
        @Override
        public FluidState getFluidState(@Nonnull BlockPos pos) {
            return level.getFluidState(pos);
        }

        @Nonnull
        @Override
        public List<Entity> getEntities(@Nullable Entity entity, @Nonnull AABB area, @Nonnull Predicate<? super Entity> predicate) {
            return level.getEntities(entity, area, predicate);
        }

        @Nonnull
        @Override
        public <T extends Entity> List<T> getEntities(@Nonnull EntityTypeTest<Entity, T> entityTypeTest, @Nonnull AABB area, @Nonnull Predicate<? super T> predicate) {
            return level.getEntities(entityTypeTest, area, predicate);
        }

        @Nonnull
        @Override
        public List<? extends Player> players() {
            return level.players();
        }

        @Nullable
        @Override
        public ChunkAccess getChunk(int pX, int pZ, @Nonnull ChunkStatus requiredStatus, boolean pNonnull) {
            return level.getChunk(pX, pZ, requiredStatus, pNonnull);
        }

        @Override
        public int getHeight(@Nonnull Heightmap.Types heightmapType, int pX, int pZ) {
            return level.getHeight(heightmapType, pX, pZ);
        }

        @Override
        public int getSkyDarken() {
            return level.getSkyDarken();
        }

        @Nonnull
        @Override
        public BiomeManager getBiomeManager() {
            return level.getBiomeManager();
        }

        @Nonnull
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

        @Nonnull
        @Override
        public DimensionType dimensionType() {
            return level.dimensionType();
        }

        @Override
        public boolean isStateAtPosition(@Nonnull BlockPos pos, @Nonnull Predicate<BlockState> state) {
            return level.isStateAtPosition(pos, state);
        }

        @Override
        public boolean isFluidAtPosition(@Nonnull BlockPos pos, @Nonnull Predicate<FluidState> predicate) {
            return level.isFluidAtPosition(pos, predicate);
        }

        @Override
        public boolean setBlock(@Nonnull BlockPos pos, @Nonnull BlockState state, int pFlags, int pRecursionLeft) {
            return level.setBlock(pos, state, pFlags, pRecursionLeft);
        }

        @Override
        public boolean removeBlock(@Nonnull BlockPos pos, boolean pIsMoving) {
            return level.removeBlock(pos, pIsMoving);
        }

        @Override
        public boolean destroyBlock(@Nonnull BlockPos pos, boolean pDropBlock, @Nullable Entity pEntity, int pRecursionLeft) {
            return level.destroyBlock(pos, pDropBlock, pEntity, pRecursionLeft);
        }
    }
}
