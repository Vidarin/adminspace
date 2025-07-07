package com.vidarin.adminspace.dimension.beyond;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ChunkGeneratorBeyond implements IChunkGenerator {
    private final World world;

    private final Biome mainBiome = BiomeInit.BEYOND_DIM;

    private final NoiseGeneratorOctaves noiseGen;

    private double[] heightMap;

    public ChunkGeneratorBeyond(World world, long seed) {
        this.world = world;

        this.noiseGen = new NoiseGeneratorOctaves(new Random(seed), 4);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();

        this.heightMap = noiseGen.generateNoiseOctaves(this.heightMap, chunkX * 16, chunkZ * 16, 16, 16, 0.25, 0.25, 1.0);

        int baseHeight = 30;
        double scaleFactor = 20.0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int noiseIndex = x + z * 16;

                int height = baseHeight + (int)(this.heightMap[noiseIndex] * scaleFactor);

                int xPos = chunkX * 16 + x;
                int zPos = chunkZ * 16 + z;

                int n00 = world.getHeight(xPos + 1, zPos);
                int n01 = world.getHeight(xPos - 1, zPos);
                int n10 = world.getHeight(xPos, zPos + 1);
                int n11 = world.getHeight(xPos, zPos - 1);

                double dx = (Math.abs(height - n00) + Math.abs(height - n01)) / 2.0;
                double dy = (Math.abs(height - n10) + Math.abs(height - n11)) / 2.0;

                int avgHeightDiff = Math.round((float) (dx + dy) / 4.0F);

                height = MathHelper.clamp(Math.abs(height - avgHeightDiff), 1, 255);

                for (int y = 0; y < height; y++) {
                    primer.setBlockState(x, y, z, BlockInit.voidBeingRock.getDefaultState());
                }
            }
        }

        Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)Biome.getIdForBiome(this.mainBiome);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {

    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
        return false;
    }
}
