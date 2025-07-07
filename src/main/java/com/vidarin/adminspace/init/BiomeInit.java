package com.vidarin.adminspace.init;

import com.vidarin.adminspace.dimension.beyond.BiomeBeyond;
import com.vidarin.adminspace.dimension.corridors.BiomeCorridors;
import com.vidarin.adminspace.dimension.deltaquest.BiomeDeltaQuestDesert;
import com.vidarin.adminspace.dimension.deltaquest.BiomeDeltaQuestForest;
import com.vidarin.adminspace.dimension.deltaquest.BiomeDeltaQuestPlains;
import com.vidarin.adminspace.dimension.deltaquest.BiomeDeltaQuestTundra;
import com.vidarin.adminspace.dimension.disposal.BiomeDisposal;
import com.vidarin.adminspace.dimension.skysector.BiomeSkySector;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BiomeInit {
    public static final Biome SKY_SECTOR_DIM = new BiomeSkySector();
    public static final Biome CORRIDOR_DIM = new BiomeCorridors();
    public static final Biome DISPOSAL_DIM = new BiomeDisposal();
    public static final Biome BEYOND_DIM = new BiomeBeyond();

    public static final Biome DELTAQUEST_PLAINS = new BiomeDeltaQuestPlains();
    public static final Biome DELTAQUEST_FOREST = new BiomeDeltaQuestForest();
    public static final Biome DELTAQUEST_DESERT = new BiomeDeltaQuestDesert();
    public static final Biome DELTAQUEST_TUNDRA = new BiomeDeltaQuestTundra();

    public static void registerBiomes() {
        initBiome(SKY_SECTOR_DIM, "Sky Sector", Type.VOID);
        initBiome(CORRIDOR_DIM, "Nowhere", Type.VOID);
        initBiome(DISPOSAL_DIM, "Depository Halls", Type.VOID);
        initBiome(BEYOND_DIM, "The Beyond", Type.VOID);

        initBiome(DELTAQUEST_PLAINS, "Plains (DQ)", Type.PLAINS);
        initBiome(DELTAQUEST_FOREST, "Forest (DQ)", Type.FOREST);
        initBiome(DELTAQUEST_DESERT, "Desert (DQ)", Type.HOT);
        initBiome(DELTAQUEST_TUNDRA, "Tundra (DQ)", Type.COLD, Type.CONIFEROUS);
    }

    private static void initBiome(Biome biome, String name, Type... types) {
        biome.setRegistryName(name);
        ForgeRegistries.BIOMES.register(biome);
        BiomeDictionary.addTypes(biome, types);
    }
}
