package com.vidarin.adminspace.dimension.beyond;

import com.vidarin.adminspace.data.AdminspaceWorldData;
import com.vidarin.adminspace.data.PlayerDataHelper;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.CPacketSinglePlayerSoundEffect;
import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.util.VisibilityUtil;
import com.vidarin.adminspace.worldgen.WorldGenStructurePlacer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MethodsReturnNonnullByDefault
public class DimensionBeyond extends WorldProvider {
    private final ArrayList<EntityPlayerMP> players = new ArrayList<>();
    private final Map<UUID, Integer> ticksInDimension = new HashMap<>();

    private boolean spawnExists = false;
    private final BlockHolder<IBlockState> spawnResetHolder = new BlockHolder<>(26, 14, 7);

    private int timeSinceWeakWorldFall = 0;
    private int timeUntilWeakWorldFall = 0;

    public DimensionBeyond() {
        this.biomeProvider = new BiomeProviderSingle(BiomeInit.BEYOND_DIM);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionInit.BEYOND;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorBeyond(this.world, this.getSeed());
    }

    @Override
    public void onPlayerAdded(@Nonnull EntityPlayerMP player) {
        super.onPlayerAdded(player);
        players.add(player);
        ticksInDimension.put(player.getUniqueID(), 0);
    }

    public static void onPlayerChangedDimension(@Nonnull EntityPlayerMP player) { // my apologies to whatever server has to run this method in a single tick
        PlayerDataHelper.setPlayerVisitedBeyond(player);
        PlayerDataHelper.sendBlindnessUpdate(player, 210);
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketSinglePlayerSoundEffect(SoundInit.BEYOND_ENTRANCE, 1F, 1F), player);

        DimensionBeyond provider = (DimensionBeyond) player.world.provider;

        provider.ticksInDimension.put(player.getUniqueID(), -275);

        BlockPos pos = generateValidSpawn(player.world);
        BlockPos structurePos = pos.add(-12, -8, -3);

        for (int x = 0; x < provider.spawnResetHolder.getXSize(); x++) {
            for (int y = 0; y < provider.spawnResetHolder.getYSize(); y++) {
                for (int z = 0; z < provider.spawnResetHolder.getZSize(); z++) {
                    provider.spawnResetHolder.set(x, y, z, player.world.getBlockState(structurePos.add(x, y, z)));
                }
            }
        }

        new WorldGenStructurePlacer("beyond/beyond_spawn", null){{ settings.setReplacedBlock(Blocks.AIR); }}
                .generate(player.world, null, structurePos);

        provider.spawnExists = true;
    }

    private static BlockPos generateValidSpawn(World world) {
        AdminspaceWorldData worldData = AdminspaceWorldData.get(world);
        int x = 0;
        while (!worldData.hasSetBeyondSpawnPos()) {
            if (x >= 256) {
                Adminspace.LOGGER.warn("Could not find valid beyond spawn point after 256 tries, using fallback spawn point.");
                return new BlockPos(0, world.getHeight(0, 0), 0);
            }
            world.getChunk(x, 0);
            worldData = AdminspaceWorldData.get(world);
            x++;
        }
        if (x != 0) Adminspace.LOGGER.info("Generated valid beyond spawn point after {} tries", x);
        return worldData.getBeyondSpawnPos();
    }

    @Override
    public void onPlayerRemoved(@Nonnull EntityPlayerMP player) {
        super.onPlayerRemoved(player);
        players.remove(player);
        ticksInDimension.remove(player.getUniqueID());
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();

        for (EntityPlayerMP player : players) {
            int ticks = ticksInDimension.get(player.getUniqueID());
            if (ticks < -273) { //Tries twice if the players client misses the first packet somehow
                BlockPos pos = AdminspaceWorldData.get(world).getBeyondSpawnPos();
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 3.0, pos.getZ() + 0.5);
            }
            if (ticks >= 0 && ticks % 960 == 0) playDimensionMusic(player);
            ticksInDimension.replace(player.getUniqueID(), ticks + 1);
        }

        if (spawnExists) {
            BlockPos pos = AdminspaceWorldData.get(world).getBeyondSpawnPos();
            if (!VisibilityUtil.isRangeBeingObserved(world, new AxisAlignedBB(pos.add(-12, -8, -3), pos.add(14, 6, 4)), VisibilityUtil.Accuracy.Varying, 300, 75)) {
                for (int x = 0; x < spawnResetHolder.getXSize(); x++) {
                    for (int y = 0; y < spawnResetHolder.getYSize(); y++) {
                        for (int z = 0; z < spawnResetHolder.getZSize(); z++) {
                            world.setBlockState(pos.add(-12 + x, -8 + y, -3 + z), spawnResetHolder.get(x, y, z));
                        }
                    }
                }
            }
        }

        if (!players.isEmpty()) {
            if (timeUntilWeakWorldFall == 0) timeUntilWeakWorldFall = world.rand.nextInt(65536) + 16384;

            if (timeSinceWeakWorldFall > timeUntilWeakWorldFall) {
                timeSinceWeakWorldFall = 0;
                timeUntilWeakWorldFall = world.rand.nextInt(65536) - 16384;

                EntityPlayerMP theChosenOne = players.get(world.rand.nextInt(players.size()));
                int x = ((int) theChosenOne.posX + world.rand.nextInt(1024) - 2048) >> 4;
                int z = ((int) theChosenOne.posZ + world.rand.nextInt(1024) - 2048) >> 4;

                ChunkGeneratorBeyond.generateWeakWorldStructure(world, new ChunkPos(x, z), x << 4, z << 4);
            }

            timeSinceWeakWorldFall++;
        }
    }

    private void playDimensionMusic(EntityPlayerMP player) {
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketSinglePlayerSoundEffect(SoundInit.SIMULATION_RUMBLING, 1F, 1F), player);
    }

    @Nullable
    @Override
    public MusicTicker.MusicType getMusicType() {
        return null;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public boolean hasSkyLight() {
        return false;
    }

    @Override
    public boolean isSkyColored() {
        return false;
    }

    @Override
    public Vec3d getSkyColor(@Nonnull Entity cameraEntity, float partialTicks) {
        return new Vec3d(0.05, 0, 0.15);
    }

    @Override
    public float getCloudHeight() {
        return 0.0f;
    }

    @Override
    public boolean doesXZShowFog(int i1, int i2) {
        return true;
    }

    @Override
    public Vec3d getFogColor(float i1, float i2) {
        return new Vec3d(0.05, 0, 0.15);
    }

    @Nullable
    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return null;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.0f;
    }
}
