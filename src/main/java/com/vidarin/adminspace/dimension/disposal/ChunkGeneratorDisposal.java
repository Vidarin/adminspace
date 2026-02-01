package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.worldgen.WorldGenBlockFiller;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class ChunkGeneratorDisposal implements IChunkGenerator {

    private final World world;
    private final Random rand;

    private ChunkPrimer chunkPrimer;

    private final Biome mainBiome = BiomeInit.DISPOSAL_DIM;

    private WorldGenBlockFiller blockFiller;

    protected ChunkGeneratorDisposal(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
        this.chunkPrimer = new ChunkPrimer();
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);

        blockFiller.fillBlocks(0, 0, 0, 15, 9, 15, Blocks.DIRT.getDefaultState());
        blockFiller.fillBlocks(0, 10, 0, 15, 10, 15, Blocks.GRASS.getDefaultState());

        int rx = rand.nextInt(13) + 1;
        int rz = rand.nextInt(13) + 1;
        int randMeta = rand.nextInt(15);

        blockFiller.fillBlocks(rx, 11, rz, rx, 13, rz, Blocks.WOOL.getStateFromMeta(randMeta));

        if (rand.nextBoolean()) {
            blockFiller.fillBlocks(rx - 1, 11, rz, rx + 1, 11, rz, Blocks.WOOL.getStateFromMeta(randMeta));
        } else {
            blockFiller.fillBlocks(rx, 11, rz - 1, rx, 11, rz + 1, Blocks.WOOL.getStateFromMeta(randMeta));
        }

        Chunk chunk = new Chunk(world, chunkPrimer, chunkX, chunkZ);

        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte)Biome.getIdForBiome(this.mainBiome);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {}

    @Override
    public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(@Nonnull Chunk chunkIn, int x, int z) {}

    @Override
    @ParametersAreNonnullByDefault
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
