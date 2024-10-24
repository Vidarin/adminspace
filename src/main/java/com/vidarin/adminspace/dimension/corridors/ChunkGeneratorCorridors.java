package com.vidarin.adminspace.dimension.corridors;

import com.vidarin.adminspace.block.BlockAxisSided;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class ChunkGeneratorCorridors implements IChunkGenerator {
    private final World world;
    private final Random rand;

    private ChunkPrimer chunkPrimer;

    private final Biome mainBiome = BiomeInit.CORRIDOR_DIM;

    private String bridgeGenerationResult;

    private final Map<ChunkPos, String> chunksToGenerate = new HashMap<>();

    protected ChunkGeneratorCorridors(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();

        bridgeGenerationResult = null;

        if (chunkX == 0 && chunkZ == 0) {
            fillBlocks(3, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            fillBlocks(0, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "A1";
        } else if (chunkX % 2 == 0 && chunkZ % 2 == 0) {
            addBridge();
        } else {
            int connectResult = canConnectBridges(chunkX, chunkZ);
            if (connectResult == 1) {
                fillBlocks(3, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
                bridgeGenerationResult = "Z2";
            } else if (connectResult == 2) {
                fillBlocks(0, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
                bridgeGenerationResult = "X2";
            } else if (connectResult == 3) {
                fillBlocks(3, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
                fillBlocks(0, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
                bridgeGenerationResult = "A2";
            }
        }

        if (bridgeGenerationResult != null) {
            decorateBridge();
            addBridgeSides(chunkX, chunkZ);
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
            fillBlocks(3, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
        int i = rand.nextInt(10);
        if (j == 0) i = -1; //Stop small bridges from generating midair
        if (i < 5 && i != -1) {
            fillBlocks(0, 10, 0, 3, 10, 12, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "X1";
        } else if (i != 9 && i != -1) {
            fillBlocks(3, 10, 12, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "Z1";
        } else if (i != -1) {
            fillBlocks(3, 10, 12, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            fillBlocks(0, 10, 0, 3, 10, 12, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "A1";
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

    private void decorateBridge() {
        if (Objects.equals(bridgeGenerationResult, "X1") || Objects.equals(bridgeGenerationResult, "X2")) { //West - East
            fillBlocks(0, 10, 6, 15, 10, 6, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.Z));
            fillBlocks(0, 10, 3, 15, 10, 3, BlockInit.corridorRailingBlock.getDefaultState());
            fillBlocks(0, 10, 9, 15, 10, 9, BlockInit.corridorRailingBlock.getDefaultState());
            fillDottedLine(0, 15, 3, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
            fillDottedLine(0, 15, 9, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
        } else if (Objects.equals(bridgeGenerationResult, "Z1") || Objects.equals(bridgeGenerationResult, "Z2")) { //North - South
            fillBlocks(9, 10, 0, 9, 10, 15, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.X));
            fillBlocks(6, 10, 0, 6, 10, 15, BlockInit.corridorRailingBlock.getDefaultState());
            fillBlocks(12, 10, 0, 12, 10, 15, BlockInit.corridorRailingBlock.getDefaultState());
            fillDottedLine(0, 15, 6, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
            fillDottedLine(0, 15, 12, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
        } else if (Objects.equals(bridgeGenerationResult, "A1") || Objects.equals(bridgeGenerationResult, "A2")) {
            fillBlocks(6, 10, 0, 12, 10, 15, BlockInit.corridorRailingBlock.getDefaultState());
            fillBlocks(0, 10, 3, 15, 10, 9, BlockInit.corridorRailingBlock.getDefaultState());
            fillDottedLine(0, 15, 6, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
            fillDottedLine(0, 15, 12, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
            fillDottedLine(0, 15, 3, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
            fillDottedLine(0, 15, 9, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
            fillBlocks(7, 10, 0, 11, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            fillBlocks(0, 10, 4, 15, 10, 8, BlockInit.corridorSmooth.getDefaultState());
            fillBlocks(0, 10, 6, 15, 10, 6, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.Z));
            fillBlocks(9, 10, 0, 9, 10, 15, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.X));
        }
    }

    private void addBridgeSides(int chunkX, int chunkZ) {

    }

    private void fillBlocks(int x1, int y1, int z1, int x2, int y2, int z2, IBlockState block) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    chunkPrimer.setBlockState(x, y, z, block);
                }
            }
        }
    }

    private void fillDottedLine(int c1, int c2, int c3, int y, int dist, boolean isX, IBlockState block) {
        int i = 0;
        for (int c = c1; c <= c2; c++) {
            if (i >= dist) {
                if (isX) chunkPrimer.setBlockState(c, y, c3, block);
                else chunkPrimer.setBlockState(c3, y, c, block);
                i = 0;
            }
            else i += 1;
        }
    }

    private void fillBlocksAfterGeneration(ChunkPos pos, int x1, int y1, int z1, int x2, int y2, int z2, IBlockState block) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    world.setBlockState(new BlockPos(x + (pos.x << 4), y, z + (pos.z << 4)), block);
                }
            }
        }
    }

    private void fillDottedLineAfterGeneration(ChunkPos pos, int c1, int c2, int c3, int y, int dist, boolean isX, IBlockState block) {
        int i = 0;
        for (int c = c1; c <= c2; c++) {
            if (i >= dist) {
                if (isX) world.setBlockState(new BlockPos(c + (pos.x << 4), y, c3 + (pos.z << 4)), block);
                else world.setBlockState(new BlockPos(c3 + (pos.x << 4), y, c + (pos.z << 4)), block);
                i = 0;
            }
            else i += 1;
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
