package com.vidarin.adminspace.dimension.corridors;

import com.vidarin.adminspace.block.BlockAxisSided;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.WorldGenBlockFiller;
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
    
    private final WorldGenBlockFiller blockFiller;

    private final Biome mainBiome = BiomeInit.CORRIDOR_DIM;

    private String bridgeGenerationResult;

    private final Map<Integer, String> chunksToDecorate = new HashMap<>();

    protected ChunkGeneratorCorridors(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();

        bridgeGenerationResult = "";

        if (chunksToDecorate.containsKey(new ChunkPos(chunkX, chunkZ).hashCode()))
            addBridgeSides(chunksToDecorate.get(new ChunkPos(chunkX, chunkZ).hashCode()));

        if (chunkX == 0 && chunkZ == 0) {
            blockFiller.fillBlocks(3, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            blockFiller.fillBlocks(0, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "A1";
        } else if (chunkX % 2 == 0 && chunkZ % 2 == 0) {
            addBridge();
        } else {
            int connectResult = canConnectBridges(chunkX, chunkZ);
            if (connectResult == 1) {
                blockFiller.fillBlocks(3, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
                bridgeGenerationResult = "Z2";
            } else if (connectResult == 2) {
                blockFiller.fillBlocks(0, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
                bridgeGenerationResult = "X2";
            } else if (connectResult == 3) {
                blockFiller.fillBlocks(3, 10, 0, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
                blockFiller.fillBlocks(0, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
                bridgeGenerationResult = "A2";
            }
        }

        if (!Objects.equals(bridgeGenerationResult, "")) {
            decorateBridge();
            prepareBridgeSides(chunkX, chunkZ);
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
            blockFiller.fillBlocks(3, 10, 0, 15, 10, 12, BlockInit.corridorSmooth.getDefaultState());
        int i = rand.nextInt(10);
        if (j == 0) i = -1; //Stop small bridges from generating midair
        if (i < 5 && i != -1) {
            blockFiller.fillBlocks(0, 10, 0, 3, 10, 12, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "X1";
        } else if (i != 9 && i != -1) {
            blockFiller.fillBlocks(3, 10, 12, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            bridgeGenerationResult = "Z1";
        } else if (i != -1) {
            blockFiller.fillBlocks(3, 10, 12, 15, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            blockFiller.fillBlocks(0, 10, 0, 3, 10, 12, BlockInit.corridorSmooth.getDefaultState());
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
            blockFiller.fillBlocks(0, 10, 6, 15, 10, 6, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.Z));
            blockFiller.fillBlocks(0, 10, 3, 15, 10, 3, BlockInit.corridorRailingBlock.getDefaultState());
            blockFiller.fillBlocks(0, 10, 9, 15, 10, 9, BlockInit.corridorRailingBlock.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 3, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 9, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
        } else if (Objects.equals(bridgeGenerationResult, "Z1") || Objects.equals(bridgeGenerationResult, "Z2")) { //North - South
            blockFiller.fillBlocks(9, 10, 0, 9, 10, 15, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.X));
            blockFiller.fillBlocks(6, 10, 0, 6, 10, 15, BlockInit.corridorRailingBlock.getDefaultState());
            blockFiller.fillBlocks(12, 10, 0, 12, 10, 15, BlockInit.corridorRailingBlock.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 6, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 12, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
        } else if (Objects.equals(bridgeGenerationResult, "A1") || Objects.equals(bridgeGenerationResult, "A2")) {
            blockFiller.fillBlocks(6, 10, 0, 12, 10, 15, BlockInit.corridorRailingBlock.getDefaultState());
            blockFiller.fillBlocks(0, 10, 3, 15, 10, 9, BlockInit.corridorRailingBlock.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 6, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 12, 10, 2, false, BlockInit.corridorLantern.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 3, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
            blockFiller.fillDottedLine(0, 15, 9, 10, 2, true, BlockInit.corridorLantern.getDefaultState());
            blockFiller.fillBlocks(7, 10, 0, 11, 10, 15, BlockInit.corridorSmooth.getDefaultState());
            blockFiller.fillBlocks(0, 10, 4, 15, 10, 8, BlockInit.corridorSmooth.getDefaultState());
            blockFiller.fillBlocks(0, 10, 6, 15, 10, 6, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.Z));
            blockFiller.fillBlocks(9, 10, 0, 9, 10, 15, BlockInit.corridorTracks.getDefaultState().withProperty(BlockAxisSided.AXIS, Axis.X));
        }
    }

    private void prepareBridgeSides(int chunkX, int chunkZ) {
        StringBuilder instructionBuilder = new StringBuilder();
        if (world.isChunkGeneratedAt(chunkX, chunkZ + 1) && (bridgeGenerationResult.contains("Z") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("gn");
        if (world.isChunkGeneratedAt(chunkX, chunkZ - 1) && (bridgeGenerationResult.contains("Z") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("gs");
        if (world.isChunkGeneratedAt(chunkX + 1, chunkZ) && (bridgeGenerationResult.contains("X") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("ge");
        if (world.isChunkGeneratedAt(chunkX - 1, chunkZ) && (bridgeGenerationResult.contains("X") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("gw");
        if (!world.isChunkGeneratedAt(chunkX, chunkZ + 1) && (bridgeGenerationResult.contains("Z") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("n");
        if (!world.isChunkGeneratedAt(chunkX, chunkZ - 1) && (bridgeGenerationResult.contains("Z") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("s");
        if (!world.isChunkGeneratedAt(chunkX + 1, chunkZ) && (bridgeGenerationResult.contains("X") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("e");
        if (!world.isChunkGeneratedAt(chunkX - 1, chunkZ) && (bridgeGenerationResult.contains("X") || bridgeGenerationResult.contains("A"))) instructionBuilder.append("w");
        if (bridgeGenerationResult.contains("1")) instructionBuilder.append("1");
        if (bridgeGenerationResult.contains("2")) instructionBuilder.append("2");

        String instruction = instructionBuilder.toString();
        ChunkPos posToAdd = new ChunkPos(chunkX, chunkZ + 1);
        while (posToAdd != null) { // This just adds all adjacent unloaded chunks to the chunksToDecorate map
            posToAdd = instruction.contains("n") && !chunksToDecorate.containsKey(new ChunkPos(chunkX, chunkZ + 1).hashCode()) ? new ChunkPos(chunkX, chunkZ + 1) : instruction.contains("s") && !chunksToDecorate.containsKey(new ChunkPos(chunkX, chunkZ - 1).hashCode()) ? new ChunkPos(chunkX, chunkZ - 1) : instruction.contains("e") && !chunksToDecorate.containsKey(new ChunkPos(chunkX + 1, chunkZ).hashCode()) ? new ChunkPos(chunkX + 1, chunkZ) : instruction.contains("w") && !chunksToDecorate.containsKey(new ChunkPos(chunkX - 1, chunkZ).hashCode()) ? new ChunkPos(chunkX - 1, chunkZ) : null; //Average ternary operator
            if (posToAdd != null) chunksToDecorate.put(posToAdd.hashCode(), instruction);
        }
        if (instruction.contains("g")) addBridgeSidesAfterGeneration(instruction, chunkX, chunkZ);
    }

    private void addBridgeSides(String instruction) {
        if (instruction.contains("n")) blockFiller.fillBlocks(0, 10, 0, 15, 10, 0, BlockInit.corridorExposedPipes.getDefaultState());
        if (instruction.contains("s")) blockFiller.fillBlocks(0, 10, 12, 15, 10, 15, BlockInit.corridorExposedPipes.getDefaultState());
        if (instruction.contains("e")) blockFiller.fillBlocks(15, 10, 0, 15, 10, 15, BlockInit.corridorExposedPipes.getDefaultState());
        if (instruction.contains("w")) blockFiller.fillBlocks(0, 10, 0, 4, 10, 15, BlockInit.corridorExposedPipes.getDefaultState());
    }

    private void addBridgeSidesAfterGeneration(String instruction, int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        if (instruction.contains("n")) blockFiller.fillBlocksAfterGeneration(pos, 0, 10, 0, 15, 10, 0, BlockInit.corridorExposedPipes.getDefaultState());
        if (instruction.contains("s")) blockFiller.fillBlocksAfterGeneration(pos, 0, 10, 12, 15, 10, 15, BlockInit.corridorExposedPipes.getDefaultState());
        if (instruction.contains("e")) blockFiller.fillBlocksAfterGeneration(pos, 15, 10, 0, 15, 10, 15, BlockInit.corridorExposedPipes.getDefaultState());
        if (instruction.contains("w")) blockFiller.fillBlocksAfterGeneration(pos, 0, 10, 0, 4, 10, 15, BlockInit.corridorExposedPipes.getDefaultState());
        chunksToDecorate.remove(pos.hashCode());
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
