package com.vidarin.adminspace.dimension.beyond;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        ticksInDimension.put(player.getUniqueID(), -275);
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
            if (ticks >= 0 && ticks % 960 == 0) {
                playDimensionMusic();
            }
            ticksInDimension.replace(player.getUniqueID(), ticks + 1);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playDimensionMusic() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMusicRecord(SoundInit.SIMULATION_RUMBLING));
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
