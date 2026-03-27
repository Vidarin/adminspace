package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.worldgen.WorldGenBlockFiller;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlock;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
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

    private final Biome mainBiome = BiomeInit.DISPOSAL_DIM;

    private final GenBlock<IBlockState> genBlock;

    protected ChunkGeneratorDisposal(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
        this.genBlock = new GenBlock<>(
                new Shape<>(
                        TestGenBlockDefinition.Symbols.LargeCube,
                        new Cube(new Vec3i(-100, 10, -100), new Vec3i(100, 110, 100))
                ),
                this.rand);

        this.genBlock.applyRules(TestGenBlockDefinition.RULES, Blocks.AIR.getDefaultState(), 10);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        WorldGenBlockFiller blockFiller = new WorldGenBlockFiller(chunkPrimer, this.world);

        blockFiller.fillBlocks(0, 0, 0, 15, 9, 15, Blocks.DIRT.getDefaultState());
        blockFiller.fillBlocks(0, 10, 0, 15, 10, 15, Blocks.GRASS.getDefaultState());

        Chunk chunk = GenBlock.extractChunk(genBlock, chunkPrimer, world, 100, new ChunkPos(chunkX, chunkZ), 0);

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
