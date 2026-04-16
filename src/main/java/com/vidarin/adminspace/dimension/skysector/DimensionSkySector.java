package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.data.AdminspaceWorldData;
import com.vidarin.adminspace.dimension.skysector.generator.ChunkGeneratorSkySector;
import com.vidarin.adminspace.dimension.skysector.generator.SectorInfo;
import com.vidarin.adminspace.dimension.skysector.generator.SkyInfo;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.CPacketSinglePlayerSoundEffect;
import com.vidarin.adminspace.util.DimTP;
import com.vidarin.adminspace.util.Vec2i;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@MethodsReturnNonnullByDefault
public class DimensionSkySector extends WorldProvider {
    private final List<EntityPlayerMP> players = new ArrayList<>();
    private final List<EntityPlayerMP> playersToTeleport = new ArrayList<>();
    private final Map<UUID, Integer> ticksInDimension = new HashMap<>();
    private Map<Vec2i, SectorInfo> sectorMap = new HashMap<>();

    public DimensionSkySector() {
        this.biomeProvider = new BiomeProviderSingle(BiomeInit.SKY_SECTOR_DIM);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionInit.SKY_SECTOR;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorSkySector(this.world, this.getSeed());
    }

    @Override
    public void onPlayerAdded(@Nonnull EntityPlayerMP player) {
        super.onPlayerAdded(player);
        players.add(player);
        ticksInDimension.put(player.getUniqueID(), 0);
    }

    @Override
    public void onPlayerRemoved(@Nonnull EntityPlayerMP player) {
        super.onPlayerRemoved(player);
        players.remove(player);
        ticksInDimension.remove(player.getUniqueID());
    }

    public void updateSectorMap(Vec2i key, SectorInfo value) {
        this.sectorMap.put(key, value);
        AdminspaceWorldData.get(this.world).putSectorToMap(key, value);
        AdminspaceWorldData.get(this.world).markDirty();
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();

        playersToTeleport.forEach(player -> DimTP.tpToDimension(player, 22, 0, 100, 0));
        playersToTeleport.clear();

        for (EntityPlayerMP player : players) {
            int ticks = ticksInDimension.get(player.getUniqueID());

            if (player.posY < -50) playersToTeleport.add(player);

            if (ticks % 4444 == 0) playDimensionMusic(player);

            ticksInDimension.replace(player.getUniqueID(), ticks + 1);

            Vec2i sectorPosition = new Vec2i((int) (player.posX / 25600), (int) (player.posZ / 25600));
            if (!sectorMap.containsKey(sectorPosition)) sectorMap = AdminspaceWorldData.get(this.world).getSkySectorMap();

            if (sectorMap.containsKey(sectorPosition)) {

                SectorInfo sector = sectorMap.get(sectorPosition);
                Vec2i skyPosition = new Vec2i((int) Math.abs((player.posX % 25600) / 3200), (int) Math.abs((player.posZ % 25600) / 3200));

                if (sector.hasSky(skyPosition)) {

                    SkyInfo sky = sector.getSky(skyPosition);
                    if (sky.state == SkyInfo.SkyState.DANGEROUS || sky.state == SkyInfo.SkyState.DESTROYED) {
                        player.sendStatusMessage(new TextComponentTranslation("message.dangerous_sky", sector.id, sky.id), true);
                    }

                } else Adminspace.LOGGER.warn("Could not find sky at position ({}, {})", skyPosition.x, skyPosition.y);

            } else Adminspace.LOGGER.warn("Could not find sector at position ({}, {})", sectorPosition.x, sectorPosition.y);
        }
    }

    private void playDimensionMusic(EntityPlayerMP player) {
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketSinglePlayerSoundEffect(SoundInit.SKY_SECTOR_MUSIC, 0.8F, 1F), player);
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
        return new Vec3d(0, 0, 0);
    }

    @Override
    public float getCloudHeight() {
        return 8.0f;
    }

    @Override
    public Vec3d getFogColor(float i1, float i2) {
        return new Vec3d(0, 0, 0);
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
