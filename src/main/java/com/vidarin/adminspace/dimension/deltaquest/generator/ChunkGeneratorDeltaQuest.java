package com.vidarin.adminspace.dimension.deltaquest.generator;

import com.vidarin.adminspace.dimension.deltaquest.GenLayerBiomesDeltaQuest;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.world.gen.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import java.util.*;
import net.minecraft.world.biome.*;
import javax.annotation.*;
import net.minecraft.init.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;

public class ChunkGeneratorDeltaQuest implements IChunkGenerator
{
    private final Random random;
    private final NoiseGeneratorOctavesDQ noiseGen1;
    private final NoiseGeneratorOctavesDQ noiseGen2;
    private final NoiseGeneratorOctavesDQ noiseGen3;
    private final NoiseGeneratorOctavesDQ noiseGen4;
    private final NoiseGeneratorOctavesDQ noiseGen5;
    public NoiseGeneratorOctavesDQ noiseGen6;
    public NoiseGeneratorOctavesDQ noiseGen7;
    public NoiseGeneratorOctavesDQ mobSpawnerNoise;
    private final World world;
    private double[] noiseArray;
    private double[] sandNoise;
    private double[] gravelNoise;
    private double[] stoneNoise;
    private final MapGenBaseDQ caveGenerator;
    double[] field_919_d;
    double[] field_918_e;
    double[] field_917_f;
    double[] field_916_g;
    double[] field_915_h;
    private Biome[] biomesForGeneration;
    
    public ChunkGeneratorDeltaQuest(final World world, final long seed) {
        this.sandNoise = new double[256];
        this.gravelNoise = new double[256];
        this.stoneNoise = new double[256];
        this.caveGenerator = new MapGenCavesDQ();
        this.world = world;
        this.random = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctavesDQ(this.random, 16);
        this.noiseGen2 = new NoiseGeneratorOctavesDQ(this.random, 16);
        this.noiseGen3 = new NoiseGeneratorOctavesDQ(this.random, 8);
        this.noiseGen4 = new NoiseGeneratorOctavesDQ(this.random, 4);
        this.noiseGen5 = new NoiseGeneratorOctavesDQ(this.random, 4);
        this.noiseGen6 = new NoiseGeneratorOctavesDQ(this.random, 10);
        this.noiseGen7 = new NoiseGeneratorOctavesDQ(this.random, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctavesDQ(this.random, 8);
    }
    
    public void generateTerrain(final int chunkX, final int chunkZ, final ChunkPrimer chunk) {
        final byte noiseZ = 4;
        final byte height = 64;
        final int width = noiseZ + 1;
        final byte depth = 17;
        final int var8 = noiseZ + 1;
        this.noiseArray = this.generateNoise(this.noiseArray, chunkX * noiseZ, 0, chunkZ * noiseZ, width, depth, var8);
        for (int var9 = 0; var9 < noiseZ; ++var9) {
            for (int var10 = 0; var10 < noiseZ; ++var10) {
                for (int var11 = 0; var11 < 16; ++var11) {
                    final double var12 = 0.125;
                    double var13 = this.noiseArray[((var9) * var8 + var10) * depth + var11];
                    double var14 = this.noiseArray[((var9) * var8 + var10 + 1) * depth + var11];
                    double var15 = this.noiseArray[((var9 + 1) * var8 + var10) * depth + var11];
                    double var16 = this.noiseArray[((var9 + 1) * var8 + var10 + 1) * depth + var11];
                    final double var17 = (this.noiseArray[((var9) * var8 + var10) * depth + var11 + 1] - var13) * var12;
                    final double var18 = (this.noiseArray[((var9) * var8 + var10 + 1) * depth + var11 + 1] - var14) * var12;
                    final double var19 = (this.noiseArray[((var9 + 1) * var8 + var10) * depth + var11 + 1] - var15) * var12;
                    final double var20 = (this.noiseArray[((var9 + 1) * var8 + var10 + 1) * depth + var11 + 1] - var16) * var12;
                    for (int var21 = 0; var21 < 8; ++var21) {
                        final double var22 = 0.25;
                        double var23 = var13;
                        double var24 = var14;
                        final double var25 = (var15 - var13) * var22;
                        final double var26 = (var16 - var14) * var22;
                        for (int var27 = 0; var27 < 4; ++var27) {
                            final int x = var27 + var9 * 4;
                            final int y = var11 * 8 + var21;
                            int z = var10 * 4;
                            final double var28 = 0.25;
                            double var29 = var23;
                            final double var30 = (var24 - var23) * var28;
                            for (int var31 = 0; var31 < 4; ++var31) {
                                Block block = null;
                                if (var11 * 8 + var21 < height) {
                                    block = Blocks.WATER;
                                }
                                if (var29 > 0.0) {
                                    block = Blocks.STONE;
                                }
                                if (block != null) {
                                    chunk.setBlockState(x, y, z, block.getDefaultState());
                                }
                                ++z;
                                var29 += var30;
                            }
                            var23 += var25;
                            var24 += var26;
                        }
                        var13 += var17;
                        var14 += var18;
                        var15 += var19;
                        var16 += var20;
                    }
                }
            }
        }
    }
    
    public void replaceBlocks(final int chunkX, final int chunkZ, final ChunkPrimer chunk) {
        final byte baseLevel = 64;
        final double height = 0.03125;
        this.sandNoise = this.noiseGen4.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, height, height, 1.0);
        this.gravelNoise = this.noiseGen4.generateNoiseOctaves(this.gravelNoise, chunkZ * 16, 109.0134, chunkX * 16, 16, 1, 16, height, 1.0, height);
        this.stoneNoise = this.noiseGen5.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, height * 2.0, height * 2.0, height * 2.0);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final boolean sand = this.sandNoise[x + z * 16] + this.random.nextDouble() * 0.2 > 0.0;
                final boolean gravel = this.gravelNoise[x + z * 16] + this.random.nextDouble() * 0.2 > 3.0;
                final int stone = (int)(this.stoneNoise[x + z * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int width = -1;
                Biome biome = biomesForGeneration[x * 16 + z];
                Block topBlock = biome.topBlock.getBlock();
                Block fillerBlock = biome.fillerBlock.getBlock();
                boolean top = true;
                for (int y = 127; y >= 0; --y) {
                    if (y <= this.random.nextInt(6) - 1) {
                        chunk.setBlockState(x, y, z, Blocks.BEDROCK.getDefaultState());
                    }
                    else {
                        final Block block = chunk.getBlockState(x, y, z).getBlock();
                        if (block == Blocks.AIR) {
                            width = -1;
                        }
                        else if (block == Blocks.STONE) {
                            if (width == -1) {
                                if (stone <= 0) {
                                    topBlock = Blocks.AIR;
                                    fillerBlock = Blocks.STONE;
                                }
                                else if (y >= baseLevel - 4 && y <= baseLevel + 1) {
                                    topBlock = biome.topBlock.getBlock();
                                    fillerBlock = biome.fillerBlock.getBlock();
                                    if (gravel) {
                                        topBlock = Blocks.AIR;
                                    }
                                    if (gravel) {
                                        fillerBlock = Blocks.GRAVEL;
                                    }
                                    if (sand) {
                                        topBlock = Blocks.SAND;
                                    }
                                    if (sand) {
                                        fillerBlock = Blocks.SAND;
                                    }
                                }
                                if (y < baseLevel && topBlock == Blocks.AIR) {
                                    topBlock = Blocks.WATER;
                                }
                                if (top) {
                                    top = false;
                                }
                                width = stone;
                                if (y >= baseLevel - 1) {
                                    chunk.setBlockState(x, y, z, topBlock.getDefaultState());
                                }
                                else {
                                    chunk.setBlockState(x, y, z, fillerBlock.getDefaultState());
                                }
                            }
                            else if (width > 0) {
                                --width;
                                chunk.setBlockState(x, y, z, fillerBlock.getDefaultState());
                            }
                        }
                        else if (block == Blocks.WATER) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.random.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
        this.biomesForGeneration = this.getBiomesForGeneration(this.biomesForGeneration, chunkX * 16, chunkZ * 16);

        ChunkPrimer chunkPrimer = new ChunkPrimer();

        this.generateTerrain(chunkX, chunkZ, chunkPrimer);
        this.replaceBlocks(chunkX, chunkZ, chunkPrimer);
        this.caveGenerator.generate(world, chunkX, chunkZ, chunkPrimer);

        Chunk chunk = new Chunk(this.world, chunkPrimer, chunkX, chunkZ);

        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i)
        {
            biomeArray[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private static final List<Biome> ALLOWED_BIOMES = Arrays.asList(
            BiomeInit.DELTAQUEST_PLAINS,
            BiomeInit.DELTAQUEST_FOREST,
            BiomeInit.DELTAQUEST_DESERT,
            BiomeInit.DELTAQUEST_TUNDRA
    );

    private @Nonnull Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z) {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < 16 * 16)
        {
            biomes = new Biome[16 * 16];
        }

        GenLayer baseLayer = new GenLayerBiomesDeltaQuest(this.world.getSeed(), ALLOWED_BIOMES);
        GenLayer deltaQuestGenBiomes = GenLayerZoom.magnify(1000L, baseLayer, 7);

        int[] biomesInts = deltaQuestGenBiomes.getInts(x, z, 16, 16);

        try
        {
            for (int i = 0; i < 16 * 16; ++i)
            {
                biomes[i] = Biome.getBiome(biomesInts[i], BiomeInit.DELTAQUEST_FOREST);
            }

            return biomes;
        }
        catch (Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }
    
    private double[] generateNoise(double[] noiseIn, final int noiseX, final int noiseY, final int noiseZ, final int height, final int width, final int depth) {
        if (noiseIn == null) {
            noiseIn = new double[height * width * depth];
        }
        final double var8 = 684.412;
        final double var9 = 684.412;
        this.field_916_g = this.noiseGen6.generateNoiseOctaves(this.field_916_g, noiseX, noiseY, noiseZ, height, 1, depth, 1.0, 0.0, 1.0);
        this.field_915_h = this.noiseGen7
.generateNoiseOctaves(this.field_915_h, noiseX, noiseY, noiseZ, height, 1, depth, 100.0, 0.0, 100.0);
        this.field_919_d = this.noiseGen3
.generateNoiseOctaves(this.field_919_d, noiseX, noiseY, noiseZ, height, width, depth, var8 / 80.0, var9 / 160.0, var8 / 80.0);
        this.field_918_e = this.noiseGen1.generateNoiseOctaves(this.field_918_e, noiseX, noiseY, noiseZ, height, width, depth, var8, var9, var8);
        this.field_917_f = this.noiseGen2.generateNoiseOctaves(this.field_917_f, noiseX, noiseY, noiseZ, height, width, depth, var8, var9, var8);
        int var10 = 0;
        int var11 = 0;
        for (int var12 = 0; var12 < height; ++var12) {
            for (int var13 = 0; var13 < depth; ++var13) {
                double var14 = (this.field_916_g[var11] + 256.0) / 512.0;
                if (var14 > 1.0) {
                    var14 = 1.0;
                }
                final double var15 = 0.0;
                double var16 = this.field_915_h[var11] / 8000.0;
                if (var16 < 0.0) {
                    var16 = -var16;
                }
                var16 = var16 * 3.0 - 3.0;
                if (var16 < 0.0) {
                    var16 /= 2.0;
                    if (var16 < -1.0) {
                        var16 = -1.0;
                    }
                    var16 /= 1.4;
                    var16 /= 2.0;
                    var14 = 0.0;
                }
                else {
                    if (var16 > 1.0) {
                        var16 = 1.0;
                    }
                    var16 /= 6.0;
                }
                var14 += 0.5;
                var16 = var16 * width / 16.0;
                final double var17 = width / 2.0 + var16 * 4.0;
                ++var11;
                for (int var18 = 0; var18 < width; ++var18) {
                    double var19;
                    double var20 = (var18 - var17) * 12.0 / var14;
                    if (var20 < 0.0) {
                        var20 *= 4.0;
                    }
                    final double var21 = this.field_918_e[var10] / 512.0;
                    final double var22 = this.field_917_f[var10] / 512.0;
                    final double var23 = (this.field_919_d[var10] / 10.0 + 1.0) / 2.0;
                    if (var23 < 0.0) {
                        var19 = var21;
                    }
                    else if (var23 > 1.0) {
                        var19 = var22;
                    }
                    else {
                        var19 = var21 + (var22 - var21) * var23;
                    }
                    var19 -= var20;
                    if (var18 > width - 4) {
                        final double var24 = (var18 - (width - 4)) / 3.0f;
                        var19 = var19 * (1.0 - var24) + -10.0 * var24;
                    }
                    if (var18 < var15) {
                        double var25 = (var15 - var18) / 4.0;
                        if (var25 < 0.0) {
                            var25 = 0.0;
                        }
                        if (var25 > 1.0) {
                            var25 = 1.0;
                        }
                        var19 = var19 * (1.0 - var25) + -10.0 * var25;
                    }
                    noiseIn[var10] = var19;
                    ++var10;
                }
            }
        }
        return noiseIn;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        BlockSand.fallInstantly = true;
        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        Biome biome = this.world.getBiome(new BlockPos(blockX + 16, 0, blockZ + 16));
        this.random.setSeed(this.world.getSeed());
        long seed1 = this.random.nextLong() / 2L * 2L + 1L;
        long seed2 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long) chunkX * seed1 + (long) chunkZ * seed2 ^ this.world.getSeed());

        // Generate water lakes
        if (this.random.nextInt(4) == 0) {
            int lakeX = blockX + this.random.nextInt(16) + 8;
            int lakeY = this.random.nextInt(128);
            int lakeZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.WATER)).generate(this.world, this.random, new BlockPos(lakeX, lakeY, lakeZ));
        }

        // Generate lava lakes
        if (this.random.nextInt(8) == 0) {
            int lavaX = blockX + this.random.nextInt(16) + 8;
            int lavaY = this.random.nextInt(this.random.nextInt(120) + 8);
            int lavaZ = blockZ + this.random.nextInt(16) + 8;
            if (lavaY < 64 || this.random.nextInt(10) == 0) {
                (new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.random, new BlockPos(lavaX, lavaY, lavaZ));
            }
        }

        // Generate dungeons
        if (this.random.nextInt(10) == 0) {
            int dungeonX = blockX + this.random.nextInt(16) + 8;
            int dungeonY = this.random.nextInt(64);
            int dungeonZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(this.world, this.random, new BlockPos(dungeonX, dungeonY, dungeonZ));
        }

        // Generate dirt
        for (int i = 0; i < 6; ++i) {
            int dirtX = blockX + this.random.nextInt(16);
            int dirtY = this.random.nextInt(128);
            int dirtZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.DIRT.getDefaultState(), 32)).generate(this.world, this.random, new BlockPos(dirtX, dirtY, dirtZ));
        }

        // Generate gravel
        for (int i = 0; i < 3; ++i) {
            int gravelX = blockX + this.random.nextInt(16);
            int gravelY = this.random.nextInt(128);
            int gravelZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), 32)).generate(this.world, this.random, new BlockPos(gravelX, gravelY, gravelZ));
        }

        // Generate coal ore
        for (int i = 0; i < 4; ++i) {
            int coalX = blockX + this.random.nextInt(16);
            int coalY = this.random.nextInt(128);
            int coalZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.COAL_ORE.getDefaultState(), 16)).generate(this.world, this.random, new BlockPos(coalX, coalY, coalZ));
        }

        // Generate iron ore
        for (int i = 0; i < 2; ++i) {
            int ironX = blockX + this.random.nextInt(16);
            int ironY = this.random.nextInt(64);
            int ironZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(), 8)).generate(this.world, this.random, new BlockPos(ironX, ironY, ironZ));
        }

        // Generate gold ore
        for (int i = 0; i < 2; ++i) {
            int goldX = blockX + this.random.nextInt(16);
            int goldY = this.random.nextInt(32);
            int goldZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(), 8)).generate(this.world, this.random, new BlockPos(goldX, goldY, goldZ));
        }

        // Generate redstone ore
        for (int i = 0; i < 1; ++i) {
            int redstoneX = blockX + this.random.nextInt(16);
            int redstoneY = this.random.nextInt(16);
            int redstoneZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.REDSTONE_ORE.getDefaultState(), 7)).generate(this.world, this.random, new BlockPos(redstoneX, redstoneY, redstoneZ));
        }

        // Generate diamond ore
        for (int i = 0; i < 1; ++i) {
            int diamondX = blockX + this.random.nextInt(16);
            int diamondY = this.random.nextInt(16);
            int diamondZ = blockZ + this.random.nextInt(16);
            (new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), 7)).generate(this.world, this.random, new BlockPos(diamondX, diamondY, diamondZ));
        }

        // Generate trees
        double treeDensity = 0.5D;
        int treeCount = (int) ((this.noiseGen6.generateNoiseOctaves(this.noiseArray, blockX, 0, blockZ, 1, 1, 1, treeDensity, 1.0D, treeDensity)[0] / 8.0D + this.random.nextDouble() * 4.0D + 4.0D) / 3.0D);
        int additionalTrees = 0;
        if (this.random.nextInt(10) == 0) {
            ++additionalTrees;
        }

        if (biome == BiomeInit.DELTAQUEST_FOREST) {
            additionalTrees += treeCount + 5;
        }

        if (biome == BiomeInit.DELTAQUEST_TUNDRA) {
            additionalTrees += treeCount + 5;
        }

        if (biome == BiomeInit.DELTAQUEST_DESERT) {
            additionalTrees -= 20;
        }

        if (biome == BiomeInit.DELTAQUEST_PLAINS) {
            additionalTrees -= 10;
        }

        WorldGenerator treeGen;
        if (biome == BiomeInit.DELTAQUEST_TUNDRA) treeGen = new WorldGenTrees(true, 4, Blocks.LOG.getDefaultState(), BlockInit.frozenLeaves.getDefaultState(), false);
        else treeGen = new WorldGenTrees(true);
        if (this.random.nextInt(10) == 0 && biome != BiomeInit.DELTAQUEST_TUNDRA) {
            treeGen = new WorldGenBigTree(true);
        }

        for (int i = 0; i < additionalTrees; ++i) {
            int treeX = blockX + this.random.nextInt(16) + 8;
            int treeZ = blockZ + this.random.nextInt(16) + 8;
            treeGen.setDecorationDefaults();
            treeGen.generate(this.world, this.random, new BlockPos(treeX, this.world.getHeight(treeX, treeZ), treeZ));
        }

        // Generate grass
        for (int i = 0; i < 4; i++) {
            int grassX = blockX + this.random.nextInt(16) + 8;
            int grassY = this.random.nextInt(128);
            int grassZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS)).generate(this.world, this.random, new BlockPos(grassX, grassY, grassZ));
        }

        // Generate flowers
        for (int i = 0; i < 2; ++i) {
            int flowerX = blockX + this.random.nextInt(16) + 8;
            int flowerY = this.random.nextInt(128);
            int flowerZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.YELLOW_FLOWER, BlockFlower.EnumFlowerType.DANDELION)).generate(this.world, this.random, new BlockPos(flowerX, flowerY, flowerZ));
        }

        if (this.random.nextInt(2) == 0) {
            int redFlowerX = blockX + this.random.nextInt(16) + 8;
            int redFlowerY = this.random.nextInt(128);
            int redFlowerZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.POPPY)).generate(this.world, this.random, new BlockPos(redFlowerX, redFlowerY, redFlowerZ));
        }

        // Generate mushrooms
        if (this.random.nextInt(4) == 0) {
            int brownMushroomX = blockX + this.random.nextInt(16) + 8;
            int brownMushroomY = this.random.nextInt(128);
            int brownMushroomZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenBush(Blocks.BROWN_MUSHROOM)).generate(this.world, this.random, new BlockPos(brownMushroomX, brownMushroomY, brownMushroomZ));
        }

        if (this.random.nextInt(8) == 0) {
            int redMushroomX = blockX + this.random.nextInt(16) + 8;
            int redMushroomY = this.random.nextInt(128);
            int redMushroomZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenBush(Blocks.RED_MUSHROOM)).generate(this.world, this.random, new BlockPos(redMushroomX, redMushroomY, redMushroomZ));
        }

        // Generate reeds
        for (int i = 0; i < 20; ++i) {
            int reedX = blockX + this.random.nextInt(16) + 8;
            int reedY = this.random.nextInt(96);
            int reedZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenReed()).generate(this.world, this.random, new BlockPos(reedX, reedY, reedZ));
        }

        // Generate pumpkins
        if (this.random.nextInt(32) == 0) {
            int pumpkinX = blockX + this.random.nextInt(16) + 8;
            int pumpkinY = this.random.nextInt(128);
            int pumpkinZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenPumpkin()).generate(this.world, this.random, new BlockPos(pumpkinX, pumpkinY, pumpkinZ));
        }

        // Generate cacti in deserts
        int cactusCount = 0;
        if (biome == BiomeInit.DELTAQUEST_DESERT) {
            cactusCount += 16;
        }

        for (int i = 0; i < cactusCount; ++i) {
            int cactusX = blockX + this.random.nextInt(16) + 8;
            int cactusY = this.random.nextInt(128);
            int cactusZ = blockZ + this.random.nextInt(16) + 8;
            (new WorldGenCactus()).generate(this.world, this.random, new BlockPos(cactusX, cactusY, cactusZ));
        }

        // Generate snow and ice
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                BlockPos snowPos = this.world.getPrecipitationHeight(new BlockPos(blockX + 8 + x, 0, blockZ + 8 + z));
                BlockPos icePos = snowPos.down();

                if (this.world.canBlockFreezeWater(icePos) || (icePos.getY() > 95 && this.world.getBlockState(icePos) == Blocks.WATER.getDefaultState())) {
                    this.world.setBlockState(icePos, Blocks.ICE.getDefaultState(), 2);
                }

                if (this.world.canSnowAt(snowPos, true)) {
                    this.world.setBlockState(snowPos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, this.random.nextInt(2) + 1), 2);
                }

                if (this.random.nextInt(5) + snowPos.getY() > 95) {
                    this.world.setBlockState(snowPos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, this.random.nextInt(3) + 1));
                }
            }
        }

        BlockSand.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(final @Nonnull Chunk chunkIn, final int x, final int z) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull List<Biome.SpawnListEntry> getPossibleCreatures(final EnumCreatureType creatureType, final BlockPos pos) {
        return this.world.getBiome(pos).getSpawnableList(creatureType);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(final @Nonnull Chunk chunkIn, final int x, final int z) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
