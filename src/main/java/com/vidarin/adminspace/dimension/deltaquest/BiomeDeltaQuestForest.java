package com.vidarin.adminspace.dimension.deltaquest;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.biome.Biome;

public class BiomeDeltaQuestForest extends Biome {
    public BiomeDeltaQuestForest() {
        super(new BiomeProperties("Forest (DQ)").setBaseHeight(1f).setHeightVariation(0f).setTemperature(0.9f).setWaterColor(1541575));

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 2, 1, 2));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 1, 3, 4));

        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 3, 2, 3));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 2, 1, 2));
    }
}
