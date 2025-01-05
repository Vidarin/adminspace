package com.vidarin.adminspace.dimension.disposal;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeDisposal extends Biome {
    public BiomeDisposal() {
        super(new BiomeProperties("Depository Halls").setBaseHeight(1f).setHeightVariation(0f).setTemperature(0.6f).setRainDisabled().setWaterColor(0));

        this.topBlock = Blocks.AIR.getDefaultState();
        this.fillerBlock = Blocks.AIR.getDefaultState();

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
    }
}
