package com.vidarin.adminspace.init;

import com.vidarin.adminspace.dimension.corridors.BiomeCorridors;
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
    public static final Biome CORRIDOR_DIM = new BiomeCorridors();

    public static void registerBiomes() {
        initBiome(SKY_SECTOR_DIM, "Sky Sector", 1, BiomeType.WARM, Type.VOID);
        initBiome(CORRIDOR_DIM, "Nowhere", 1, BiomeType.COOL, Type.VOID);
    }

    private static void initBiome(Biome biome, String name, int weight, BiomeType biomeType, Type... types) {
        biome.setRegistryName(name);
        ForgeRegistries.BIOMES.register(biome);
        BiomeDictionary.addTypes(biome, types);
        BiomeManager.addBiome(biomeType, new BiomeEntry(biome, weight));
    }
}
