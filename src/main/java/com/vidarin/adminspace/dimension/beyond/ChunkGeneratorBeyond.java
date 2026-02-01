package com.vidarin.adminspace.dimension.beyond;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.FastNoiseLite;
import com.vidarin.adminspace.worldgen.WorldGenStructurePlacer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

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

    private final FastNoiseLite spikeNoiseGen;
    private final FastNoiseLite detailNoiseGen;

    public ChunkGeneratorBeyond(World world, long seed) {
        this.world = world;

        this.spikeNoiseGen = new FastNoiseLite(seed);
        this.spikeNoiseGen.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        this.spikeNoiseGen.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        this.spikeNoiseGen.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Sub);
        this.spikeNoiseGen.SetCellularJitter(0.7F);
        this.spikeNoiseGen.SetFrequency(0.035F);

        this.detailNoiseGen = new FastNoiseLite(seed);
        this.detailNoiseGen.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.detailNoiseGen.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.detailNoiseGen.SetFractalOctaves(4);
        this.detailNoiseGen.SetFractalLacunarity(1.35F);
        this.detailNoiseGen.SetFractalGain(1F);
        this.detailNoiseGen.SetFrequency(0.12F);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();

        int baseHeight = 60;
        double scaleFactor = 50.0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int worldX = (chunkX << 4) + x;
                int worldZ = (chunkZ << 4) + z;

                int height = baseHeight + (int) (spikeNoiseGen.GetNoise(worldX, worldZ) * scaleFactor);

                double detailHeight = (Math.abs(detailNoiseGen.GetNoise(worldX, worldZ)) - 0.5) * (20.0 / (double) height) * (scaleFactor / 3.0);

                height += Math.toIntExact(Math.round(detailHeight));

                if (height < 1) height = 1;

                for (int y = 0; y < height; y++) {
                    primer.setBlockState(x, y, z, BlockInit.voidBeingRock.getDefaultState());
                }
            }
        }

        Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);

        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte) Biome.getIdForBiome(this.mainBiome);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private static final int WEAK_WORLD_STRUCTURES = 15;

    @Override
    public void populate(int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;

        if (world.rand.nextFloat() < 0.02) {
            int i = world.rand.nextInt(WEAK_WORLD_STRUCTURES) + 1;
            new WorldGenStructurePlacer("beyond/beyond_weak_world_" + i, pos, 23){{ settings.setReplacedBlock(Blocks.AIR); }}
                    .generateWithRotation(world, new BlockPos(blockX, world.rand.nextInt((int) Math.round(world.getHeight(blockX, blockZ) * 0.9)), blockZ), randomRotation(world.rand));
        }
    }

    private static Rotation randomRotation(Random rand) {
        return Rotation.values()[rand.nextInt(Rotation.values().length)];
    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {return false;}

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {return Collections.emptyList();}

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored) {return null;}

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {}

    @Override
    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {return false;}
}
