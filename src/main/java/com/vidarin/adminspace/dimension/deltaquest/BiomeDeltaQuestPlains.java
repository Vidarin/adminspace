package com.vidarin.adminspace.dimension.deltaquest;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public class BiomeDeltaQuestPlains extends Biome {
    public BiomeDeltaQuestPlains() {
        super(new BiomeProperties("Plains (DQ)").setBaseHeight(1f).setHeightVariation(0f).setTemperature(1.2f).setWaterColor(1541575));

        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 3, 2, 5));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 5, 1, 1));

        this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 4, 2, 6));
        this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 2, 1, 4));
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
