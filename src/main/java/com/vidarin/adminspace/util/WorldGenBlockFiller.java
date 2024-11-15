package com.vidarin.adminspace.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class WorldGenBlockFiller {
    private final ChunkPrimer chunkPrimer;
    private final World world;

    public WorldGenBlockFiller(ChunkPrimer chunkPrimer, World world) {
        this.chunkPrimer = chunkPrimer;
        this.world = world;
    }

    public void fillBlocks(int x1, int y1, int z1, int x2, int y2, int z2, IBlockState block) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    chunkPrimer.setBlockState(x, y, z, block);
                }
            }
        }
    }

    public void fillDottedLine(int c1, int c2, int c3, int y, int dist, boolean isX, IBlockState block) {
        int i = 0;
        for (int c = c1; c <= c2; c++) {
            if (i >= dist) {
                if (isX) chunkPrimer.setBlockState(c, y, c3, block);
                else chunkPrimer.setBlockState(c3, y, c, block);
                i = 0;
            }
            else i += 1;
        }
    }

    public void fillBlocksAfterGeneration(ChunkPos pos, int x1, int y1, int z1, int x2, int y2, int z2, IBlockState block) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockPos blockPos = new BlockPos(x + (pos.x << 4), y, z + (pos.z << 4));
                    if (world.isBlockLoaded(blockPos))
                        if (world.getBlockState(blockPos) == Blocks.AIR.getDefaultState())
                            world.setBlockState(blockPos, block);
                }
            }
        }
    }

    public void fillDottedLineAfterGeneration(ChunkPos pos, int c1, int c2, int c3, int y, int dist, boolean isX, IBlockState block) {
        int i = 0;
        for (int c = c1; c <= c2; c++) {
            if (i >= dist) {
                if (isX) world.setBlockState(new BlockPos(c + (pos.x << 4), y, c3 + (pos.z << 4)), block);
                else world.setBlockState(new BlockPos(c3 + (pos.x << 4), y, c + (pos.z << 4)), block);
                i = 0;
            }
            else i += 1;
        }
    }
}
