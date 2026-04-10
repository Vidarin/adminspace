package com.vidarin.adminspace.dimension.deltaquest;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeDeltaQuestDesert extends Biome {
    public BiomeDeltaQuestDesert() {
        super(new BiomeProperties("Desert (DQ)").setBaseHeight(1f).setHeightVariation(0f).setTemperature(2.2f).setWaterColor(1541575).setRainDisabled());

        this.topBlock = Blocks.SAND.getDefaultState();
        this.fillerBlock = Blocks.SANDSTONE.getDefaultState();

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 3, 1, 2));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityHusk.class, 2, 2, 4));

        this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 2, 1, 3));
    }
}
