package com.vidarin.adminspace.dimension;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nonnull;
import java.util.List;

public class GenLayerCustomBiomes extends GenLayer {
    private final List<Biome> allowedBiomes;

    public GenLayerCustomBiomes(long seed, List<Biome> allowedBiomes) {
        super(seed);
        this.allowedBiomes = allowedBiomes;
    }

    @Override
    public @Nonnull int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] result = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaWidth; ++y) {
            for (int x = 0; x < areaHeight; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                result[x + y * areaWidth] = Biome.getIdForBiome(
                        allowedBiomes.get(this.nextInt(allowedBiomes.size()))
                );
            }
        }

        return result;
    }
}

