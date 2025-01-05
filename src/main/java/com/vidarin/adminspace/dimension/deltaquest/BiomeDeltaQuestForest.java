package com.vidarin.adminspace.dimension.deltaquest;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public class BiomeDeltaQuestForest extends Biome {
    public BiomeDeltaQuestForest() {
        super(new BiomeProperties("Forest (DQ)").setBaseHeight(1f).setHeightVariation(0f).setTemperature(0.8f).setWaterColor(1541575));

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
    }

    @Override
    public int getFoliageColorAtPos(@Nonnull BlockPos pos) {
        return 0x48F84C;
    }

    @Override
    public int getModdedBiomeFoliageColor(int original) {
        return 0x48F84C;
    }

    @Override
    public int getGrassColorAtPos(@Nonnull BlockPos pos) {
        return 0x48F84C;
    }

    @Override
    public int getModdedBiomeGrassColor(int original) {
        return 0x48F84C;
    }
}
