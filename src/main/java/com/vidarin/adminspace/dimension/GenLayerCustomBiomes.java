package com.vidarin.adminspace.dimension;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nonnull;
import java.util.List;

public class GenLayerCustomBiomes extends GenLayer {
    private final List<Biome> allowedBiomes;

    public GenLayerCustomBiomes(long seed, List<Biome> allowedBiomes) {
        super(seed);
        this.allowedBiomes = allowedBiomes;
        this.parent = new GenLayerIsland(seed);
    }

    @Override
    public @Nonnull int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] result = IntCache.getIntCache(areaWidth * areaHeight);

        for (int x = 0; x < areaWidth; ++x) {
            for (int y = 0; y < areaHeight; ++y) {
                this.initChunkSeed(x + areaX, (y + areaY));
                result[x + y * areaWidth] = Biome.getIdForBiome(
                        allowedBiomes.get(this.nextInt(allowedBiomes.size()))
                );
            }
        }

        return result;
    }
}

