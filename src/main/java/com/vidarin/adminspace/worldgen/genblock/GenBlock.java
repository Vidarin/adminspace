package com.vidarin.adminspace.worldgen.genblock;

import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.worldgen.grammar.RuleSet;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenBlock<T> {
    private final Random rand;

    private final BlockNode<T> node;
    private final BlockHolder<T> blocks;

    public GenBlock(Shape<Cube> baseShape, Random rand) {
        this.rand = rand;

        this.node = new BlockNode<>(baseShape, new ArrayList<>());

        this.blocks = new BlockHolder<>(baseShape.shape().to().getX(), baseShape.shape().to().getY(), baseShape.shape().to().getZ());
    }

    public void applyRule(RuleSet<Cube, Pair<BlockHolder<T>, Cube>> rules) {
        List<BlockNode<T>> children = node.lowestChildren();
        for (BlockNode<T> child : children) {
            if (child.cube().symbol().isTerminal()) continue;

            Shape<Pair<BlockHolder<T>, Cube>> shape = rules.get(child.cube(), this.rand);

            BlockHolder<T> blockHolder = shape.shape().getLeft();
            Cube cube = shape.shape().getRight();

            for (int x = 0; x < blockHolder.getXSize(); x++) {
                for (int y = 0; y < blockHolder.getYSize(); y++) {
                    for (int z = 0; z < blockHolder.getZSize(); z++) {
                        this.blocks.set(
                                cube.from().getX() + x,
                                cube.from().getY() + y,
                                cube.from().getZ() + z,
                                blockHolder.get(x, y, z)
                        );
                    }
                }
            }

            Shape<Cube> cubeShape = new Shape<>(shape.symbol(), cube, shape.metadata());
            child.children().add(new BlockNode<>(cubeShape, new ArrayList<>()));
        }
    }

    public BlockHolder<T> extractRange(int xSize, int ySize, int zSize, int xOff, int yOff, int zOff, T fallback) {
        BlockHolder<T> blockHolder = new BlockHolder<>(xSize, ySize, zSize);
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    blockHolder.set(
                            x, y, z,
                            this.blocks.getOrElse(x + xOff * xSize, y + yOff * ySize, z + zOff * zSize, fallback)
                    );
                }
            }
        }

        return blockHolder;
    }

    @SuppressWarnings("unchecked")
    public Chunk extractChunk(World world, int height, ChunkPos pos, int yOff) {
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        BlockHolder<IBlockState> blocks = ((BlockHolder<IBlockState>) this.blocks);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < 16; z++) {
                    chunkPrimer.setBlockState(
                            x, y, z,
                            blocks.getOrElse(x + (pos.x << 4), y + yOff, z + (pos.z << 4), Blocks.AIR.getDefaultState())
                    );
                }
            }
        }

        return new Chunk(world, chunkPrimer, pos.x, pos.z);
    }
}
