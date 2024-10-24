package com.vidarin.adminspace.dimension.corridors;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@MethodsReturnNonnullByDefault
public class DimensionCorridors extends WorldProvider {
    private final ArrayList<EntityPlayerMP> players = new ArrayList<>();
    private final Map<UUID, Integer> ticksInDimension = new HashMap<>();
    private final Map<UUID, Integer> prevRenderDist = new HashMap<>();
    private final Map<UUID, Float> prevBrightness = new HashMap<>();

    public DimensionCorridors() {
        this.biomeProvider = new BiomeProviderSingle(BiomeInit.CORRIDOR_DIM);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionInit.CORRIDORS;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorCorridors(this.world, this.getSeed());
    }

    @Override
    public void onPlayerAdded(@Nonnull EntityPlayerMP player) {
        super.onPlayerAdded(player);
        players.add(player);
        ticksInDimension.put(player.getUniqueID(), 0);
        System.out.println("Player " + player.getName() + " joined!");
        getPrevPlayerSettings(player);
    }

    @SideOnly(Side.CLIENT)
    private void getPrevPlayerSettings(EntityPlayer player) {
        if (player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID())) {
            prevRenderDist.put(player.getUniqueID(), Minecraft.getMinecraft().gameSettings.renderDistanceChunks);
            prevBrightness.put(player.getUniqueID(), Minecraft.getMinecraft().gameSettings.gammaSetting);
        }
    }

    @Override
    public void onPlayerRemoved(@Nonnull EntityPlayerMP player) {
        super.onPlayerRemoved(player);
        players.remove(player);
        ticksInDimension.remove(player.getUniqueID());
        resetPlayerSettings(player);
        prevRenderDist.remove(player.getUniqueID());
        prevBrightness.remove(player.getUniqueID());
    }

    @SideOnly(Side.CLIENT)
    private void resetPlayerSettings(EntityPlayer player) {
        if (player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID())) {
            Minecraft.getMinecraft().gameSettings.renderDistanceChunks = prevRenderDist.get(player.getUniqueID());
            Minecraft.getMinecraft().gameSettings.gammaSetting = prevBrightness.get(player.getUniqueID());
        }
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();
        for (EntityPlayerMP player : players) {
            if (ticksInDimension.get(player.getUniqueID()) % 2222 == 0) {
                playDimensionMusic();
            }
            ticksInDimension.replace(player.getUniqueID(), ticksInDimension.get(player.getUniqueID()) + 1);
            updatePlayerSettings(player);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playDimensionMusic() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMusicRecord(SoundInit.CORRIDOR_MUSIC));
    }

    @SideOnly(Side.CLIENT)
    private void updatePlayerSettings(EntityPlayer player) {
        if (player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID())) {
            if (Minecraft.getMinecraft().gameSettings.renderDistanceChunks > 2)
                Minecraft.getMinecraft().gameSettings.renderDistanceChunks = 2;
            if (Minecraft.getMinecraft().gameSettings.gammaSetting > 0)
                Minecraft.getMinecraft().gameSettings.gammaSetting = 0;
            if (Minecraft.getMinecraft().world.provider.getMusicType() != null)
                Minecraft.getMinecraft().getSoundHandler().stopSound(PositionedSoundRecord.getMusicRecord(Minecraft.getMinecraft().world.provider.getMusicType().getMusicLocation()));
        }
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
        return new Vec3d(0, 0, 0.5);
    }

    @Override
    public float getCloudHeight() {
        return 8.0f;
    }

    @Override
    public Vec3d getFogColor(float i1, float i2) {
        return new Vec3d(0, 0, 0.5);
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
