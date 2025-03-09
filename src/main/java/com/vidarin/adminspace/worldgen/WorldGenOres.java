package com.vidarin.adminspace.worldgen;

import com.vidarin.adminspace.dimension.deltaquest.DimensionDeltaQuest;
import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenOres implements IWorldGenerator {
    private final WorldGenerator noiseGem, rainbow; // Ores
    private final WorldGenerator voidHoleDeltaQuest; // Void holes

    public WorldGenOres() {
        noiseGem = new WorldGenMinable(BlockInit.noiseGemOre.getDefaultState(), 9);
        rainbow = new WorldGenMinable(BlockInit.rainbowOre.getDefaultState(), 4);

        voidHoleDeltaQuest = new WorldGenMinable(BlockInit.squirmingOrganism.getDefaultState(), 20, BlockMatcher.forBlock(Blocks.BEDROCK));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider instanceof DimensionDeltaQuest) {
            runGenerator(noiseGem, world, random, chunkX, chunkZ, 1, 5, 25);
            runGenerator(rainbow, world, random, chunkX, chunkZ, 1, 3, 12);
            if (random.nextInt(3) == 0)
                runGenerator(voidHoleDeltaQuest, world, random, chunkX, chunkZ, 1, 0, 3);
        }
    }

    private void runGenerator(WorldGenerator generator, World world, Random rand, int chunkX, int chunkZ, int chance, int minHeight, int maxHeight) {
        int heightDiff = maxHeight - minHeight + 1;
        for (int i = 0; i < chance; i++) {
            int x = chunkX * 16 + rand.nextInt(16);
            int y = minHeight + rand.nextInt(heightDiff);
            int z = chunkZ * 16 + rand.nextInt(16);

            generator.generate(world, rand, new BlockPos(x, y, z));
        }
    }
}
