package com.vidarin.adminspace.init;

import com.vidarin.adminspace.dimension.skysector.BiomeSkySector;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BiomeInit {
    public static final Biome SKY_SECTOR_DIM = new BiomeSkySector();

    public static void registerBiomes() {
        initBiome(SKY_SECTOR_DIM, "Sky Sector", 10, BiomeType.WARM, Type.VOID);
    }

    private static Biome initBiome(Biome biome, String name, int weight, BiomeType biomeType, Type... types) {
        biome.setRegistryName(name);
        ForgeRegistries.BIOMES.register(biome);
        System.out.println("Biome " + name + " Registered!");
        BiomeDictionary.addTypes(biome, types);
        BiomeManager.addBiome(biomeType, new BiomeEntry(biome, weight));
        System.out.println("Biome " + name + " Added!");
        return biome;
    }
}
