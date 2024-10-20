package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@MethodsReturnNonnullByDefault
public class DimensionSkySector extends WorldProvider {
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
