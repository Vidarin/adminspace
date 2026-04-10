package com.vidarin.adminspace.dimension.deltaquest;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.biome.Biome;

public class BiomeDeltaQuestTundra extends Biome {
    public BiomeDeltaQuestTundra() {
        super(new BiomeProperties("Tundra (DQ)").setBaseHeight(1f).setHeightVariation(0f).setTemperature(0.0f).setWaterColor(1541575).setSnowEnabled().setRainfall(0.5f));

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 1, 3, 5));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityStray.class, 2, 1, 2));

        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 2, 1, 2));

    }
}
