package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BiomeSkySector extends Biome {
    public BiomeSkySector() {
        super(new BiomeProperties("Sky Sector").setBaseHeight(1f).setHeightVariation(0f).setTemperature(0.6f).setRainDisabled().setWaterColor(0));

        this.topBlock = BlockInit.skyGround.getDefaultState();
        this.fillerBlock = BlockInit.skyGround.getDefaultState();

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
    }
}
