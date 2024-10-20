package com.vidarin.adminspace.dimension.corridors;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeCorridors extends Biome {
    public BiomeCorridors() {
        super(new BiomeProperties("Nowhere").setBaseHeight(0f).setHeightVariation(0f).setTemperature(0f).setRainDisabled());

        this.topBlock = Blocks.AIR.getDefaultState();
        this.fillerBlock = Blocks.AIR.getDefaultState();

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
    }
}
