package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.StructurePlacer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorSkySector implements IChunkGenerator {
    private final World world;
    private final Random rand;

    private ChunkPrimer chunkPrimer;

    private final Biome mainBiome = BiomeInit.SKY_SECTOR_DIM;

    private int x, y, z;

    private static boolean hasGeneratedSpawn = false;
    private static int rooms = 0;

    // Locking flag
    private static boolean isPopulating = false;

    protected ChunkGeneratorSkySector(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();

        fillBlocks(0, 0, 0, 15, 31, 15, BlockInit.skyGround.getDefaultState());

        for (x = 0; x <= 15; x++) {
            for (z = 0; z <= 15; z++) {
                if (rand.nextInt(10) == 0)
                    chunkPrimer.setBlockState(x, 31, z, BlockInit.skyGround2.getDefaultState());
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
    public void populate(int x, int z) {
        if (isPopulating) {
            return;
        }

        isPopulating = true;
        try {
            if (!hasGeneratedSpawn) {
                new StructurePlacer("skysector/sky_spawn").generate(world, rand, new BlockPos(0, 31, 0));
                world.getChunkFromBlockCoords(new BlockPos(0, 0, 0)).generateSkylightMap();
                hasGeneratedSpawn = true;
            } else if (x != 0 || z != 0) {
                int i = rand.nextInt(7);
                if (i == 0 || rooms >= 20) {
                    rooms -= 1;
                } else {
                    new StructurePlacer("skysector/sky_corridor_" + i).generateWithRotation(world, new BlockPos(x * 16, 31, z * 16), randomRotation());
                    rooms += 1;
                }
            }
        } finally {
            isPopulating = false;
        }
    }

    private Rotation randomRotation() {
        int i = rand.nextInt(4);
        switch (i) {
            case 0:
                return Rotation.NONE;
            case 1:
                return Rotation.CLOCKWISE_90;
            case 2:
                return Rotation.CLOCKWISE_180;
            case 3:
                return Rotation.COUNTERCLOCKWISE_90;
        }
        return Rotation.NONE;
    }

    @Override
    public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.world.getBiome(pos).getSpawnableList(creatureType);
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
