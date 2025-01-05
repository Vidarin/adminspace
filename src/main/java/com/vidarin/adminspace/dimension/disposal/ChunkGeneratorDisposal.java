package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.worldgen.WorldGenBlockFiller;
import com.vidarin.adminspace.worldgen.WorldGenStructurePlacer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
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

public class ChunkGeneratorDisposal implements IChunkGenerator {
    private final Map<ChunkPos, String> megastructureMap = new HashMap<>();

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
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);

        blockFiller.fillBlocks(0, 0, 0, 15, 1, 15, Blocks.BEDROCK.getDefaultState());

        if (chunkX == 0 && chunkZ == 0)
            for (int z = 1; z <= 8; z++) {
                for (int x = 0; x < 8; x++) {
                    megastructureMap.put(new ChunkPos(x, z), "skysector/sky_megastructure_terminal_" + ((x * 8) + z));
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

    @Override
    public void populate(int x, int z) {
        if (megastructureMap.containsKey(new ChunkPos(x, z)))
            new WorldGenStructurePlacer(megastructureMap.get(new ChunkPos(x, z))).generate(world, rand, new BlockPos(x * 16, 1, z * 16));
    }

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
