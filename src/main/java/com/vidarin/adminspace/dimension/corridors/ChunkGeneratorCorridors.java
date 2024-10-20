package com.vidarin.adminspace.dimension.corridors;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorCorridors implements IChunkGenerator {
    private final World world;
    private final Random rand;

    private ChunkPrimer chunkPrimer;

    private final Biome mainBiome = BiomeInit.CORRIDOR_DIM;

    private int x, y, z;

    protected ChunkGeneratorCorridors(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();

        if (chunkX == 0 && chunkZ == 0) {
            fillBlocks(0, 10, 0, 15, 10, 11, BlockInit.corridorSmooth.getDefaultState());
            fillBlocks(4, 10, 11, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
        }

        if (chunkX % 2 == 0 && chunkZ % 2 == 0) {
            addBridge();
        } else {
            int connectResult = canConnectBridges(chunkX, chunkZ);
            if (connectResult == 1) {
                fillBlocks(4, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            } else if (connectResult == 2) {
                fillBlocks(0, 10, 0, 15, 10, 11, BlockInit.corridorSmooth.getDefaultState());
            } else if (connectResult == 3) {
                fillBlocks(4, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            }
        }

        Chunk chunk = new Chunk(world, chunkPrimer, chunkX, chunkZ);

        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte)Biome.getIdForBiome(this.mainBiome);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private void addBridge() {
        int j = rand.nextInt(3);
        if (j != 0)
            fillBlocks(4, 10, 0, 15, 10, 11, BlockInit.corridorSmooth.getDefaultState());
        int i = rand.nextInt(10);
        if (j == 0) i = -1; //Stop small bridges from generating midair
        if (i < 5 && i != -1)
            fillBlocks(0, 10, 0, 4, 10, 11, BlockInit.corridorSmooth.getDefaultState());
        else if (i != 9 && i != -1)
            fillBlocks(4, 10, 11, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
        else if (i != -1){
            fillBlocks(0, 10, 0, 4, 10, 11, BlockInit.corridorSmooth.getDefaultState());
            fillBlocks(4, 10, 11, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
        }
    }

    private int canConnectBridges(int chunkX, int chunkZ) {
        int result = 0;

        boolean canConnectNS = false;
        boolean canConnectWE = false;

        if (world.isChunkGeneratedAt(chunkX, chunkZ + 1)) {
            Chunk northChunk = world.getChunkFromChunkCoords(chunkX, chunkZ + 1);
            if (checkBlockState(northChunk, 15, 15)) {
                canConnectNS = true;
            }
        }

        if (world.isChunkGeneratedAt(chunkX + 1, chunkZ)) {
            Chunk eastChunk = world.getChunkFromChunkCoords(chunkX + 1, chunkZ);
            if (checkBlockState(eastChunk, 0, 0)) {
                canConnectWE = true;
            }
        }

        if (world.isChunkGeneratedAt(chunkX, chunkZ - 1)) {
            Chunk southChunk = world.getChunkFromChunkCoords(chunkX, chunkZ - 1);
            if (checkBlockState(southChunk, 15, 15)) {
                canConnectNS = true;
            }
        }

        if (world.isChunkGeneratedAt(chunkX - 1, chunkZ)) {
            Chunk westChunk = world.getChunkFromChunkCoords(chunkX - 1, chunkZ);
            if (checkBlockState(westChunk, 0, 0)) {
                canConnectWE = true;
            }
        }

        if (canConnectNS) result += 1;
        if (canConnectWE) result += 2;

        return result;
    }

    private boolean checkBlockState(Chunk chunk, int x, int z) {
        IBlockState blockState = chunk.getBlockState(x, 10, z);
        return blockState.getBlock() == BlockInit.corridorSmooth;
    }

    private void fillBlocks(int x1, int y1, int z1, int x2, int y2, int z2, IBlockState block) {
        for (x = x1; x <= x2; x++) {
            for (y = y1; y <= y2; y++) {
                for (z = z1; z <= z2; z++) {
                    chunkPrimer.setBlockState(x, y, z, block);
                }
            }
        }
    }

    @Override
    public void populate(int x, int z) {}

    @Override
    public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {return false;}

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {return Collections.emptyList();}

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {return null;}

    @Override
    public void recreateStructures(@Nonnull Chunk chunkIn, int x, int z) {}

    @Override
    @ParametersAreNonnullByDefault
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {return false;}
}
