package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.worldgen.WorldGenBlockFiller;
import com.vidarin.adminspace.worldgen.WorldGenStructurePlacer;
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

public class ChunkGeneratorSkySector implements IChunkGenerator {
    private final World world;
    private final Random rand;

    private ChunkPrimer chunkPrimer;

    private final Biome mainBiome = BiomeInit.SKY_SECTOR_DIM;

    private WorldGenBlockFiller blockFiller;

    private static boolean chunkNeedsStructure;

    private final Map<ChunkPos, String> megastructureMap = new HashMap<>();

    protected ChunkGeneratorSkySector(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
        this.chunkPrimer = new ChunkPrimer();
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        this.chunkPrimer = new ChunkPrimer();
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);

        String sector = getSector(chunkX, chunkZ);

        blockFiller.fillBlocks(0, 0, 0, 15, 8, 15, BlockInit.skyGround.getDefaultState());

        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                if (rand.nextInt(10) == 0) {
                    chunkPrimer.setBlockState(x, 8, z, BlockInit.skyGround2.getDefaultState());
                }
            }
        }

        if (!sector.equals("empty")) {
            blockFiller.fillBlocks(0, 8, 0, 15, 8, 15, BlockInit.voidTile.getDefaultState());
            if ((rand.nextFloat() < 0.0012 && (chunkX % 200 < 191 && chunkZ % 200 < 191)) && canPlaceMegastructure(chunkX, chunkZ))
                for (int z = 1; z <= 8; z++) {
                    for (int x = 0; x < 8; x++) {
                        megastructureMap.put(new ChunkPos(chunkX + x, chunkZ + z), "skysector/sky_megastructure_terminal_" + ((x * 8) + z));
                    }
                }
        }
        if (sector.equals("corridor")) {
            blockFiller.fillBlocks(0, 8, 0, 0, 23, 15, BlockInit.voidTile.getDefaultState());
            blockFiller.fillBlocks(0, 8, 0, 15, 23, 0, BlockInit.voidTile.getDefaultState());
            blockFiller.fillBlocks(0, 24, 0, 15, 24, 15, BlockInit.voidTile.getDefaultState());
            if (!megastructureMap.containsKey(new ChunkPos(chunkX, chunkZ))) {
                addCubesToChunk();
                if (rand.nextFloat() < 0.08)
                    chunkNeedsStructure = true;
            }
        } if (sector.equals("spawn")) {
            blockFiller.fillBlocks(0, 24, 0, 15, 24, 15, BlockInit.voidTile.getDefaultState());
        }

        Chunk chunk = new Chunk(world, chunkPrimer, chunkX, chunkZ);

        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte)Biome.getIdForBiome(this.mainBiome);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private String getSector(int chunkX, int chunkZ) {
        int x = Math.abs(chunkX);
        int z = Math.abs(chunkZ);

        if (x % 200 == 0 && z % 200 == 0)
            return "spawn";
        else if ((x % 200 < 10 && z % 200 < 10) || (x % 200 > 190 && z % 200 > 190) || (x % 200 < 10 && z % 200 > 190) || (x % 200 > 190 && z % 200 < 10))
            return "corridor";
        else if ((x % 200 < 20 && z % 200 < 20) || (x % 200 > 180 && z % 200 > 180) || (x % 200 < 20 && z % 200 > 180) || (x % 200 > 180 && z % 200 < 20))
            return "outside";
        else
            return "empty";
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        int x = Math.abs(chunkX);
        int z = Math.abs(chunkZ);

        if (x % 200 == 0 && z % 200 == 0) {
            new WorldGenStructurePlacer("skysector/sky_spawn").generate(world, rand, new BlockPos(chunkX * 16, 8, chunkZ * 16));
        }
        else if (((x % 200 < 10 && z % 200 < 10) || (x % 200 > 190 && z % 200 > 190) || (x % 200 < 10 && z % 200 > 190) || (x % 200 > 190 && z % 200 < 10)) && chunkNeedsStructure) {
            int i = rand.nextInt(14) + 1;
            new WorldGenStructurePlacer("skysector/sky_corridor_" + i).generate(world, rand, new BlockPos(chunkX * 16, 8, chunkZ * 16));
            chunkNeedsStructure = false;
        }

        if (megastructureMap.containsKey(new ChunkPos(chunkX, chunkZ)))
            new WorldGenStructurePlacer(megastructureMap.get(new ChunkPos(chunkX, chunkZ))).generate(world, rand, new BlockPos(chunkX * 16, 8, chunkZ * 16));
    }

    private boolean canPlaceMegastructure(int chunkX, int chunkZ) {
        for (int z = chunkZ + 1; z < chunkZ + 9; z++) {
            for (int x = chunkX; x < chunkX + 8; x++) {
                if (world.isChunkGeneratedAt(x, z)) return false;
                if (megastructureMap.containsKey(new ChunkPos(x, z))) return false;
            }
        }
        return true;
    }

    private final List<CubeTypes> all = new ArrayList<>(Arrays.asList(CubeTypes.values()));
    private final List<CubeTypes> solid = Arrays.asList(CubeTypes.Filled, CubeTypes.Lamp, CubeTypes.FullLamp, CubeTypes.ErrFilled, CubeTypes.ErrLamp);

    private final List<CubeTypes> ladder = Arrays.asList(CubeTypes.Ladder, CubeTypes.Ladder, CubeTypes.LadderTop);
    private final List<CubeTypes> bottomOnly = Arrays.asList(CubeTypes.DisposalEntrance, CubeTypes.VoidObservationDeck, CubeTypes.Empty4Way, CubeTypes.Empty3Way, CubeTypes.Empty2Way);

    private final Map<Vec3i, CubeTypes> placedCubes = new HashMap<>(); // Vec3i is just to store 3 values

    private void addCubesToChunk() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 5; z++) {
                    CubeTypes currentCube;
                    if (y == 0 && z == 2 && ( x == 0 || x == 4)) currentCube = CubeTypes.Empty; // Forces entrances and exits to exist
                    else currentCube = getRandomCubeType(getValidList(x, y, z), x, y, z);
                    blockFiller.fillBlocks(x * 3 + 1, y * 3 + 9, z * 3 + 1, x * 3 + 3, y * 3 + 11, z * 3 + 3, currentCube.getMainBlock());
                    if (currentCube == CubeTypes.Ladder && y == 4) currentCube = CubeTypes.LadderTop; // Makes ladders look a bit nicer.
                    if (currentCube.getDetail() != CubeTypes.DetailTypes.None) {
                        for (int x2 = 0; x2 < 3; x2++) {
                            for (int y2 = 0; y2 < 3; y2++) {
                                for (int z2 = 0; z2 < 3; z2++) {
                                    BlockPos pos = new BlockPos(x2, y2, z2);
                                    IBlockState blockToPlace;
                                    if (y2 == 0)
                                        blockToPlace = currentCube.getDetail().getBottomBlockFromBlockPos(pos);
                                    else if (y2 == 1)
                                        blockToPlace = currentCube.getDetail().getMiddleBlockFromBlockPos(pos);
                                    else
                                        blockToPlace = currentCube.getDetail().getTopBlockFromBlockPos(pos);
                                    if (blockToPlace != Blocks.AIR.getDefaultState())
                                        chunkPrimer.setBlockState(x * 3 + 1 + x2, y * 3 + 9 + y2, z * 3 + 1 + z2, blockToPlace);
                                }
                            }
                        }
                    }
                    if (!currentCube.getSpecial().isEmpty()) {
                        switch (currentCube.getSpecial()) {
                            case "add_doors":
                                if (x == 0)
                                    blockFiller.fillBlocks(0, y * 3 + 9, z * 3 + 1, 0, y * 3 + 11, z * 3 + 3, Blocks.AIR.getDefaultState());
                                if (z == 0)
                                    blockFiller.fillBlocks(x * 3 + 1, y * 3 + 9, 0, x * 3 + 3, y * 3 + 11, 0, Blocks.AIR.getDefaultState());
                                break;
                            case "tunnel_to_disposal":
                                blockFiller.fillBlocks(x * 3 + 2, 0, z * 3 + 2, x * 3 + 2, 9, z * 3 + 2, Blocks.AIR.getDefaultState());
                                break;
                            case "glass_floor":
                                blockFiller.fillBlocks(x * 3 + 1, y * 3 + 8, z * 3 + 1, x * 3 + 3, y * 3 + 8, z * 3 + 3, BlockInit.voidGlass.getDefaultState());
                                break;
                        }
                    }
                    placedCubes.put(new Vec3i(x, y, z), currentCube);
                }
            }
        }
        placedCubes.clear();
    }

    private List<CubeTypes> getValidList(int x, int y, int z) {
        CubeTypes downAdjacentCube = CubeTypes.Filled;
        List<CubeTypes> allValid = all;

        for (int i = 0; i < allValid.size(); i++)
            if (ladder.contains(allValid.get(i)) || bottomOnly.contains(allValid.get(i)))
                allValid.remove(allValid.get(i));

        if (placedCubes.isEmpty()) return allValid;

        if (placedCubes.containsKey(new Vec3i(x, y - 1, z))) downAdjacentCube = placedCubes.get(new Vec3i(x, y - 1, z));

        if (downAdjacentCube == CubeTypes.LadderBottom || downAdjacentCube == CubeTypes.Ladder) return ladder;
        if (y == 0 && rand.nextFloat() < 0.02) return bottomOnly;

        return allValid;
    }

    private CubeTypes getRandomCubeType(List<CubeTypes> cubeList, int x, int y, int z) {
        int index = rand.nextInt(cubeList.size());
        int tries = 0;
        while (!isCubeValid(x, y, z, cubeList.get(index)) && tries < 100) {
            index = rand.nextInt(cubeList.size());
            tries++;
        }
        if (tries >= 100) return CubeTypes.Filled;
        return cubeList.get(index);
    }

    private boolean isCubeValid(int x, int y, int z, CubeTypes cube) {
        CubeTypes northAdjacentCube = placedCubes.getOrDefault(new Vec3i(x + 1, y, z), CubeTypes.Filled);
        CubeTypes southAdjacentCube = placedCubes.getOrDefault(new Vec3i(x - 1, y, z), CubeTypes.Filled);
        CubeTypes westAdjacentCube  = placedCubes.getOrDefault(new Vec3i(x, y, z + 1), CubeTypes.Filled);
        CubeTypes eastAdjacentCube  = placedCubes.getOrDefault(new Vec3i(x, y, z - 1), CubeTypes.Filled);
        CubeTypes upAdjacentCube    = placedCubes.getOrDefault(new Vec3i(x, y + 1, z), CubeTypes.Filled);
        CubeTypes downAdjacentCube  = placedCubes.getOrDefault(new Vec3i(x, y - 1, z), CubeTypes.Filled);

        // Ladder stuff (always allows ladders and does not allow ladders to start on the top floor)
        if (ladder.contains(cube) && (downAdjacentCube == CubeTypes.LadderBottom || downAdjacentCube == CubeTypes.Ladder)) return true;
        if (cube == CubeTypes.LadderBottom && y == 4) return false;

        // If two non-solid cubes doesn't connect 'true-true' return false
        if (!solid.contains(cube)) {
            if (!solid.contains(northAdjacentCube) && !(northAdjacentCube.isSouth() && cube.isNorth())) return false;
            if (!solid.contains(southAdjacentCube) && !(southAdjacentCube.isNorth() && cube.isSouth())) return false;
            if (!solid.contains(westAdjacentCube) && !(westAdjacentCube.isEast() && cube.isWest())) return false;
            if (!solid.contains(eastAdjacentCube) && !(eastAdjacentCube.isWest() && cube.isEast())) return false;
            if (!solid.contains(upAdjacentCube) && !(upAdjacentCube.isDown() && cube.isUp())) return false;
            if (!solid.contains(downAdjacentCube) && !(downAdjacentCube.isUp() && cube.isDown())) return false;
        }

        // If a solid and another cube connects 'true-false' return false
        if (solid.contains(cube)) {
            if ((northAdjacentCube.isSouth() && !cube.isNorth()) || (!northAdjacentCube.isSouth() && cube.isNorth())) return false;
            if ((southAdjacentCube.isNorth() && !cube.isSouth()) || (!southAdjacentCube.isNorth() && cube.isSouth())) return false;
            if ((westAdjacentCube.isEast() && !cube.isWest()) || (!westAdjacentCube.isEast() && cube.isWest())) return false;
            if ((eastAdjacentCube.isWest() && !cube.isEast()) || (!eastAdjacentCube.isWest() && cube.isEast())) return false;
            if ((upAdjacentCube.isDown() && !cube.isUp()) || (!upAdjacentCube.isDown() && cube.isUp())) return false;
            if ((downAdjacentCube.isUp() && !cube.isDown()) || (!downAdjacentCube.isUp() && cube.isDown())) return false;
        }

        // Ensures floors are separated
        if (!solid.contains(downAdjacentCube) && !solid.contains(cube))
            if (!downAdjacentCube.isUp()) return false;
        if (!solid.contains(upAdjacentCube) && !solid.contains(cube))
            if (!upAdjacentCube.isDown()) return false;

        return rand.nextInt(100) < cube.getWeight(); // Lower weight means higher chance of being invalid
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
