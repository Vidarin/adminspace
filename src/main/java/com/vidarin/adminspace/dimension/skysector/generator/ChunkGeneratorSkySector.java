package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.data.AdminspaceWorldData;
import com.vidarin.adminspace.dimension.skysector.DimensionSkySector;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.FastNoiseLite;
import com.vidarin.adminspace.util.Vec2i;
import com.vidarin.adminspace.worldgen.WorldGenBlockFiller;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlock;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
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
    private Map<Vec2i, SectorInfo> sectorMap = new HashMap<>();

    private SkyInfo currentSky;

    private final World world;
    private final Random rand;

    private final Biome mainBiome = BiomeInit.SKY_SECTOR_DIM;

    private final FastNoiseLite groundNoiseGen;

    public ChunkGeneratorSkySector(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.world.setSeaLevel(0);
        this.groundNoiseGen = new FastNoiseLite(seed);
        this.groundNoiseGen.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.groundNoiseGen.SetFrequency(0.03f);
        this.groundNoiseGen.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.groundNoiseGen.SetFractalOctaves(2);
    }

    @Override
    public @Nonnull Chunk generateChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        WorldGenBlockFiller blockFiller = new WorldGenBlockFiller(chunkPrimer, this.world);

        Vec2i sectorPosition = new Vec2i(chunkX / 1600, chunkZ / 1600);
        this.checkSectorMap(chunkX, chunkZ, sectorPosition);

        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                int worldX = (chunkX << 4) + x;
                int worldZ = (chunkZ << 4) + z;

                int height = isBlockPosInStructure(worldX, worldZ) ? 8 : getHeight(sectorPosition, worldX, worldZ);
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

        Chunk chunk = GenBlock.extractChunk(this.currentSky.genBlock, chunkPrimer, world, 180, new ChunkPos(chunkX, chunkZ), 8);

        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte) Biome.getIdForBiome(this.mainBiome);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private boolean isBlockPosInStructure(int x, int z) {
        Vec3i from = this.currentSky.genBlock.from();
        Vec3i to = this.currentSky.genBlock.to();
        return x >= from.getX() && x < to.getX() && z >= from.getZ() && z < to.getZ();
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
            if (this.world.provider instanceof DimensionSkySector skySector) {
                skySector.updateSectorMap(position, new SectorInfo(position, activityLevels[index]));
                Adminspace.LOGGER.debug("Created Sector with activity level {}", activityLevels[index]);
            }
        }
        SectorInfo sector = sectorMap.get(position);
        Vec2i skyPosition = new Vec2i(Math.abs((x % 1600) / 200), Math.abs((z % 1600) / 200));
        if (!sector.hasSky(skyPosition)) {
            SkyInfo.SkyState state = getSkyState(sector.activityLevel.getSeverity());
            int sizeX = rand.nextInt(400) + 200;
            int sizeZ = rand.nextInt(400) + 200;
            GenBlock<IBlockState> genBlock;
            if (state == SkyInfo.SkyState.ABSENT) {
                genBlock = new GenBlock<>(new Shape<>(SkySectorGenBlockDefinition.Symbols.EntryPoint, Cube.fromSizeAndOffset(
                        new Vec3i(1, 1, 1), new Vec3i(x << 4, 6, z << 4)), new int[]{0, 0}), this.rand);
            } else {
                genBlock = new GenBlock<>(new Shape<>(SkySectorGenBlockDefinition.Symbols.EntryPoint, Cube.fromSizeAndOffset(
                        new Vec3i(sizeX, 180, sizeZ), new Vec3i(x << 4, 6, z << 4)
                ), new int[]{sector.activityLevel.ordinal(), state.ordinal()}), this.rand);
                genBlock.applyRules(SkySectorGenBlockDefinition.RULES, Blocks.AIR.getDefaultState(), (int) Math.sqrt((sizeX / 10f) * (sizeZ / 10f)));
            }
            currentSky = sector.createSky(skyPosition, state, genBlock);
            if (this.world.provider instanceof DimensionSkySector skySector) {
                skySector.updateSectorMap(position, sector);
            }
        }
        else currentSky = sector.getSky(skyPosition);
    }

    private SkyInfo.SkyState getSkyState(int severity) {
        int abandonedChance = (int) Math.pow(severity, 2) * 2 + severity;
        int dangerousChance = (int) MathHelper.clamp(Math.pow(severity - 2, 3), 1, 99);
        int absentChance = (severity * 8) + 5;
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

    private int getHeight(Vec2i position, int x, int z) {
        if (sectorMap.containsKey(position)) {
            int heightVariation = (sectorMap.get(position).activityLevel.getHeightVariation() + 1);
            int heightDiff = Math.round((groundNoiseGen.GetNoise(x, z) + 0.2f) * heightVariation);
            return 8 + heightDiff;
        }
        else return 8;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {}

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
