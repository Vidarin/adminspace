package com.vidarin.adminspace.worldgen;

import com.vidarin.adminspace.dimension.deltaquest.DimensionDeltaQuest;
import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenDataDaisy implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!(world.provider instanceof DimensionDeltaQuest)) return;

        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        for (int i = 0; i < 10; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = 5 + random.nextInt(60);

            BlockPos center = new BlockPos(x, y, z);

            int floorY = -1;
            for (int dy = 0; dy <= 10; dy++) {
                BlockPos pos = center.down(dy);
                if (!world.isAirBlock(pos)) {
                    floorY = pos.getY();
                    break;
                }
            }

            if (floorY == -1) continue;

            int ceilingY = -1;
            for (int dy = 0; dy <= 10; dy++) {
                BlockPos pos = center.up(dy);
                if (!world.isAirBlock(pos)) {
                    ceilingY = pos.getY();
                    break;
                }
            }

            if (ceilingY == -1) continue;

            int height = ceilingY - floorY - 1;
            if (height <= 0 || height > 10) continue;

            for (int y2 = floorY + 1; y2 < ceilingY; y2++) {
                BlockPos pos = new BlockPos(x, y2, z);
                world.setBlockState(pos, BlockInit.dataDaisy.getDefaultState(), 2);
            }
        }
    }
}
