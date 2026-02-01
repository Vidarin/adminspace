package com.vidarin.adminspace.dimension.skysector.generator;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.data.AdminspaceWorldData;
import com.vidarin.adminspace.dimension.skysector.DimensionSkySector;
import com.vidarin.adminspace.dimension.skysector.generator.CellTypes.Connections.Connection;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.util.Vec2i;
import com.vidarin.adminspace.worldgen.WorldGenBlockFiller;
import com.vidarin.adminspace.worldgen.WorldGenStructurePlacer;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
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
    private Map<Vec2i, SectorInfo> sectorMap = new HashMap<>();

    private final List<ChunkPos> insideStructureChunks = new ArrayList<>();
    private final List<ChunkPos> spawnStructureChunks = new ArrayList<>();

    private final Map<ChunkPos, List<DeferredBlock>> deferredBlockMap = new HashMap<>();

    private SkyInfo currentSky;

    /** chunkType values:   <br>
    * 0: nothing            <br>
    * 1: inside             <br>
    * 2: outside            <br>
    * 3: structure (inside) <br>
    * 4: structure (outside)<br>
    * 5: destroyed          <br>
     */
    private byte chunkType;

    private final World world;
    private final Random rand;

    private ChunkPrimer chunkPrimer;

    private final Biome mainBiome = BiomeInit.SKY_SECTOR_DIM;

    private WorldGenBlockFiller blockFiller;

    public ChunkGeneratorSkySector(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
        this.chunkPrimer = new ChunkPrimer();
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.chunkPrimer = new ChunkPrimer();
        this.blockFiller = new WorldGenBlockFiller(this.chunkPrimer, this.world);

        Vec2i sectorPosition = new Vec2i(chunkX / 1600, chunkZ / 1600);
        this.checkSectorMap(chunkX, chunkZ, sectorPosition);
        this.setChunkType(chunkX, chunkZ);

        ChunkPos pos = new ChunkPos(chunkX, chunkZ);

        if (chunkType == 0 || chunkType == 5) {
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    int height = getHeight(sectorPosition);
                    blockFiller.fillBlocks(x, 0, z, x, height, z, BlockInit.skyGround.getDefaultState());

                    if (rand.nextInt(10) == 0) {
                        chunkPrimer.setBlockState(x, height, z, BlockInit.skyGround2.getDefaultState());
                    }
                    if (sectorMap.containsKey(sectorPosition)) {
                        if (sectorMap.get(sectorPosition).activityLevel.shouldPlaceSquirmingOrganism(this.rand)) {
                            chunkPrimer.setBlockState(x, height, z, BlockInit.squirmingOrganism.getDefaultState());
                        }
                    }

                }
            }
        } else {
            blockFiller.fillBlocks(0, 0, 0, 15, 7, 15, BlockInit.skyGround.getDefaultState());
            blockFiller.fillBlocks(0, 8, 0, 15, 8, 15, BlockInit.voidTile.getDefaultState());

            int distX = Math.abs(chunkX % 200) - 100;
            int distZ = Math.abs(chunkZ % 200) - 100;

            if (distX == 0 && distZ == 0) {
                spawnStructureChunks.add(pos);
            } else if (chunkType == 1) {
                blockFiller.fillBlocks(0, 8, 0, 0, 27, 15, BlockInit.voidTile.getDefaultState());
                blockFiller.fillBlocks(0, 8, 0, 15, 27, 0, BlockInit.voidTile.getDefaultState());
                for (int i = 0; i < 3; i++) {
                    CellTypes[][] grid = switch (i) {
                        case 0 -> this.currentSky.bottomGrid;
                        case 1 -> this.currentSky.middleGrid;
                        default -> this.currentSky.topGrid;
                    };
                    for (int x = 0; x < 5; x++) {
                        for (int z = 0; z < 5; z++) {
                            int gridX = (distX + 10) * 5 + x;
                            int gridY = (distZ + 10) * 5 + z;
                            placeCell(chunkPrimer, gridX, gridY, grid, i * 6, new ChunkPos(chunkX, chunkZ));
                        }
                    }
                }
            } else if (chunkType == 3) {
                insideStructureChunks.add(pos);
                blockFiller.fillBlocks(0, 24, 0, 15, 27, 15, BlockInit.voidTile.getDefaultState());
            }

            if (distX == 20) blockFiller.fillBlocks(15, 8, 0, 15, 12, 15, BlockInit.voidTile.getDefaultState());
            if (distZ == 20) blockFiller.fillBlocks(0, 8, 15, 15, 12, 15, BlockInit.voidTile.getDefaultState());
            if (distX == -20) blockFiller.fillBlocks(0, 8, 0, 0, 12, 15, BlockInit.voidTile.getDefaultState());
            if (distZ == -20) blockFiller.fillBlocks(0, 8, 0, 15, 12, 0, BlockInit.voidTile.getDefaultState());
        }

        Chunk chunk = new Chunk(world, chunkPrimer, chunkX, chunkZ);

        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte) Biome.getIdForBiome(this.mainBiome);
        }

        List<DeferredBlock> deferredBlocks = deferredBlockMap.remove(pos);
        if (deferredBlocks != null) {
            for (DeferredBlock b : deferredBlocks) {
                BlockPos local = new BlockPos(b.pos.getX() & 15, b.pos.getY(), b.pos.getZ() & 15);
                chunk.setBlockState(local, b.state);
            }
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private void placeCell(ChunkPrimer primer, int gridX, int gridY, CellTypes[][] grid, int yOffset, ChunkPos pos) {
        int cellX = (gridX % 5) * 3;
        int cellZ = (gridY % 5) * 3;

        CellTypes cell = grid[gridX][gridY];
        if ((gridX == 106 || gridY == 106) && cell != CellTypes.Wall) {
            if (rand.nextInt(128) != 0) cell = CellTypes.Wall;
        }
        Connection connection = getConnection(grid, gridX, gridY);

        BlockHolder<IBlockState> blocks = cell.getInnerBlocks().getBlocks(connection.getDirection(rand));

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    IBlockState state = blocks.get(x, y, z);
                    if (state.getBlock() instanceof ITileEntityProvider) {
                        deferredBlockMap.computeIfAbsent(pos, k -> new ArrayList<>())
                                        .add(new DeferredBlock(new BlockPos(cellX + x + 1, 9 + y + yOffset, cellZ + z + 1), state));
                    } else primer.setBlockState(cellX + x + 1, 9 + y + yOffset, cellZ + z + 1, state);
                }
            }
        }

        blockFiller.fillBlocks(cellX + 1, 12 + yOffset, cellZ + 1, cellX + 3, 15 + yOffset, cellZ + 3, BlockInit.voidTile.getDefaultState());
        deferredBlockMap.computeIfAbsent(pos, k -> new ArrayList<>())
                .add(new DeferredBlock(new BlockPos(cellX + 2, 12 + yOffset, cellZ + 2), BlockInit.voidLamp.getDefaultState()));

        if (cellX == 0 && !cell.isSolid()) blockFiller.fillBlocks(0, 9 + yOffset, cellZ + 1, 0, 11 + yOffset, cellZ + 3, Blocks.AIR.getDefaultState());
        if (cellZ == 0 && !cell.isSolid()) blockFiller.fillBlocks(cellX + 1, 9 + yOffset, 0, cellX + 3, 11 + yOffset, 0, Blocks.AIR.getDefaultState());
    }

    private void setChunkType(int chunkX, int chunkZ) {
        int distX = Math.abs(Math.abs(chunkX % 200) - 100);
        int distZ = Math.abs(Math.abs(chunkZ % 200) - 100);
        if (distX > 20 && distZ > 20 || currentSky.state == SkyInfo.SkyState.ABSENT) chunkType = 0;
        else if (currentSky.state == SkyInfo.SkyState.DESTROYED && distX <= 20 && distZ <= 20) chunkType = 5;
        else if (distX <= 10 && distZ <= 10) {
            if (rand.nextInt(20) == 0) chunkType = 3;
            else chunkType = 1;
        }
        else if (distX <= 20 && distZ <= 20) {
            if (rand.nextInt(30) == 0) chunkType = 4;
            else chunkType = 2;
        }
        else chunkType = 0;
    }

    private void checkSectorMap(int x, int z, Vec2i position) {
        if (sectorMap.isEmpty()) {
            this.sectorMap = AdminspaceWorldData.get(world).getSkySectorMap();
            Adminspace.LOGGER.debug("Chunk generator read the sector map successfully!");
        }
        if (!sectorMap.containsKey(position)) {
            SectorInfo.ActivityLevel[] activityLevels = SectorInfo.ActivityLevel.values();
            int index = 0;
            for (int i = 0; i < activityLevels.length - 1; i++) {
                if (this.rand.nextBoolean()) index++;
                else break;
            }
            sectorMap.put(position, new SectorInfo(position, activityLevels[index]));
            if (this.world.provider instanceof DimensionSkySector) {
                ((DimensionSkySector)this.world.provider).updateSectorMap(position, new SectorInfo(position, activityLevels[index]));
                Adminspace.LOGGER.debug("Created Sector with activity level {}", activityLevels[index]);
            }
        }
        SectorInfo sector = sectorMap.get(position);
        Vec2i skyPosition = new Vec2i(Math.abs((x % 1600) / 200), Math.abs((z % 1600) / 200));
        if (!sector.hasSky(skyPosition)) {
            currentSky = sector.createSky(skyPosition, getSkyState(sector.activityLevel.getSeverity()), () -> generateCorridorGrid(106, 106, 4));
            if (this.world.provider instanceof DimensionSkySector) {
                ((DimensionSkySector)this.world.provider).updateSectorMap(position, sector);
            }
        }
        else currentSky = sector.getSky(skyPosition);
    }

    private SkyInfo.SkyState getSkyState(int severity) {
        int abandonedChance = (int) Math.pow(severity, 2) * 2 + severity;
        int dangerousChance = (int) MathHelper.clamp(Math.pow(severity - 2, 3), 1, 99);
        int absentChance = (severity * 8) + 10;
        int destroyedChance = (int) MathHelper.clamp(Math.pow(severity - 3, 5) / (severity * 2), 1, 99);
        int roll = rand.nextInt(100);

        List<SkyInfo.SkyState> possibleStates = new ArrayList<>();

        if (roll < absentChance) possibleStates.add(SkyInfo.SkyState.ABSENT);
        if (roll < dangerousChance) possibleStates.add(SkyInfo.SkyState.DANGEROUS);
        if (roll < abandonedChance) possibleStates.add(SkyInfo.SkyState.ABANDONED);
        if (roll < destroyedChance) possibleStates.add(SkyInfo.SkyState.DESTROYED);
        else possibleStates.add(SkyInfo.SkyState.OPERATIONAL);

        return possibleStates.get(rand.nextInt(possibleStates.size()));
    }

    private int getHeight(Vec2i position) {
        if (sectorMap.containsKey(position)) {
            int heightVariation = sectorMap.get(position).activityLevel.getHeightVariation() + 1;
            int heightDiff = (rand.nextInt(heightVariation)) - ((heightVariation / 2) - 1);
            return 6 + heightDiff;
        }
        else return 8;
    }

    private static final int[][] DIRECTIONS = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};

    @SuppressWarnings("SameParameterValue")
    private CellTypes[][] generateCorridorGrid(int width, int height, final int entryCount) {
        if (width % 2 == 0) width++;
        if (height % 2 == 0) height++;

        final CellTypes[][] grid = new CellTypes[height][width];
        for (CellTypes[] row : grid) Arrays.fill(row, CellTypes.Wall);

        final LongList frontier = new LongArrayList();
        final LongSet visited = new LongOpenHashSet();

        for (int i = 0; i < entryCount; i++) {
            final int x = 1 + (rand.nextInt((width - 1) >> 1) << 1);
            final int y = 1 + (rand.nextInt((height - 1) >> 1) << 1);
            grid[y][x] = CellTypes.Path;
            final long pos = ((long) x << 32L) | y;
            frontier.add(pos);
            visited.add(pos);
        }

        while (!frontier.isEmpty()) {
            final int index = rand.nextInt(frontier.size());
            final long current = frontier.remove(index);
            final int cx = Math.toIntExact((current >>> 32L) & 0xFFFFFFFFL);
            final int cy = Math.toIntExact(current & 0xFFFFFFFFL);

            final List<int[]> shuffledDirs = new ArrayList<>(Arrays.asList(DIRECTIONS));
            Collections.shuffle(shuffledDirs);

            for (int[] dir : shuffledDirs) {
                final int nx = cx + dir[0];
                final int ny = cy + dir[1];
                final int mx = cx + (dir[0] >> 1);
                final int my = cy + (dir[1] >> 1);

                if (nx > 0 && ny > 0 && nx < width - 1 && ny < height - 1) {
                    long key = ((long) nx << 32L) | ny;

                    if (!visited.contains(key) && grid[ny][nx].isSolid()) {
                        grid[ny][nx] = CellTypes.Path;
                        grid[my][mx] = CellTypes.Path;
                        frontier.add(key);
                        visited.add(key);
                    }
                }
            }
        }

        this.addSpecials(grid, 200 + rand.nextInt(100));
        this.addExits(grid, 4 + rand.nextInt(4));
        return grid;
    }


    private void addSpecials(final CellTypes[][] grid, final int count) {
        for (int i = 0; i < count; i++) {
            final int y = rand.nextInt(grid.length);
            final int x = rand.nextInt(grid[0].length);
            final CellTypes.Connections.Connection connection = getConnection(grid, x, y);
            grid[y][x] = CellTypes.randomSpecial(rand, connection, grid[y][x].isSolid());
        }
    }

    private CellTypes.Connections.Connection getConnection(final CellTypes[][] grid, final int x, final int y) {
        return new CellTypes.Connections.Connection(
                x != 0 && !grid[y][x - 1].isSolid(),
                x < grid[y].length - 1 && !grid[y][x + 1].isSolid(),
                y != 0 && !grid[y - 1][x].isSolid(),
                y < grid.length - 1 && !grid[y + 1][x].isSolid()
        );
    }

    private void addExits(final CellTypes[][] grid, final int count) {
        final int height = grid.length;
        final int width = grid[0].length;
        final List<int[]> candidates = new ArrayList<>();

        for (int x = 1; x < width - 1; x += 2) {
            candidates.add(new int[]{x, 0});
            candidates.add(new int[]{x, height - 1});
        }
        for (int y = 1; y < height - 1; y += 2) {
            candidates.add(new int[]{0, y});
            candidates.add(new int[]{width - 1, y});
        }

        Collections.shuffle(candidates, rand);
        for (int i = 0; i < Math.min(count, candidates.size()); i++) {
            final int[] pos = candidates.get(i);
            grid[pos[1]][pos[0]] = CellTypes.Path;
        }
    }

    private static final int CORRIDOR_STRUCTURES = 16;

    @Override
    public void populate(int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);

        if (insideStructureChunks.contains(pos)) {
            int i = rand.nextInt(CORRIDOR_STRUCTURES) + 1;
            new WorldGenStructurePlacer("skysector/sky_corridor_" + i, pos, 20)
                    .generate(world, rand, new BlockPos(chunkX << 4, 8, chunkZ << 4));
            insideStructureChunks.remove(pos);
        }
        if (spawnStructureChunks.contains(pos)) {
            new WorldGenStructurePlacer("skysector/sky_spawn", pos, 20)
                    .generate(world, rand, new BlockPos(chunkX << 4, 8, chunkZ << 4));
            spawnStructureChunks.remove(pos);
        }
    }

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

    @Desugar
    private record DeferredBlock(BlockPos pos, IBlockState state) {}
}
