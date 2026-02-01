package com.vidarin.adminspace.dimension.beyond;

import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.world.biome.Biome;

public class BiomeBeyond extends Biome {
    public BiomeBeyond() {
        super(new BiomeProperties("The Beyond").setBaseHeight(1f).setHeightVariation(0f).setTemperature(0.6f).setRainDisabled());

        this.topBlock = BlockInit.voidBeingRock.getDefaultState();
        this.fillerBlock = BlockInit.voidBeingRock.getDefaultState();


        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
    }
}
