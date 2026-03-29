package com.vidarin.adminspace.worldgen.genblock;

import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.util.NBTSerializer;
import com.vidarin.adminspace.worldgen.grammar.RuleSet;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GenBlock<T> {
    private Random rand;

    private final BlockNode node;
    private final BlockHolder<T> blocks;

    public GenBlock(Shape<Cube> baseShape, Random rand) {
        this(
                rand,
                new BlockNode(baseShape, new ArrayList<>()),
                new BlockHolder<>(
                        baseShape.shape().sizeX(), baseShape.shape().sizeY(), baseShape.shape().sizeZ(),
                        baseShape.shape().from().getX(), baseShape.shape().from().getY(), baseShape.shape().from().getZ()
                )
        );
    }

    private GenBlock(Random rand, BlockNode node, BlockHolder<T> blocks) {
        this.rand = rand;
        this.node = node;
        this.blocks = blocks;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public void applyRules(RuleSet<Cube, Pair<BlockHolder<T>, Cube>> rules, @Nullable T empty, int amount) {
        for (int i = 0; i < amount; i++) {
            applyRules(rules, empty);
        }
    }

    public void applyRules(RuleSet<Cube, Pair<BlockHolder<T>, Cube>> rules, @Nullable T empty) {
        List<BlockNode> children = node.lowestChildren();
        for (BlockNode child : children) {
            if (child.cube().symbol().isTerminal()) continue;

            Iterable<Shape<Pair<BlockHolder<T>, Cube>>> shapes = rules.get(child.cube(), this.rand);

            if (shapes == null) continue;

            if (empty != null) {
                this.blocks.fill(
                        child.cube().shape().from().getX(),
                        child.cube().shape().from().getY(),
                        child.cube().shape().from().getZ(),
                        child.cube().shape().to().getX(),
                        child.cube().shape().to().getY(),
                        child.cube().shape().to().getZ(),
                        empty
                );
            }

            for (Shape<Pair<BlockHolder<T>, Cube>> shape : shapes) {
                BlockHolder<T> blockHolder = shape.shape().getLeft();
                Cube cube = shape.shape().getRight();

                this.blocks.copyFrom(blockHolder, cube.from().getX(), cube.from().getY(), cube.from().getZ());

                Shape<Cube> cubeShape = new Shape<>(shape.symbol(), cube, shape.metadata());
                child.children().add(new BlockNode(cubeShape, new ArrayList<>()));
            }
        }
    }

    public BlockHolder<T> extractAll() {
        return BlockHolder.copyOf(blocks);
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

    public static Chunk extractChunk(GenBlock<IBlockState> genBlock, ChunkPrimer chunkPrimer, World world, int height, ChunkPos pos, int worldYOff) {
        if (pos.x < (genBlock.blocks.getXOff() << 4) || pos.z < (genBlock.blocks.getZOff() << 4)) return new Chunk(world, chunkPrimer, pos.x, pos.z);
        if (pos.x > ((genBlock.blocks.getXSize() + genBlock.blocks.getXOff()) << 4) || pos.z > ((genBlock.blocks.getZSize() + genBlock.blocks.getZOff()) << 4)) return new Chunk(world, chunkPrimer, pos.x, pos.z);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < 16; z++) {
                    IBlockState fallback = chunkPrimer.getBlockState(x, y + worldYOff, z);
                    chunkPrimer.setBlockState(
                            x, y + worldYOff, z,
                            genBlock.blocks.getOrElse(x + (pos.x << 4), y, z + (pos.z << 4), fallback)
                    );
                }
            }
        }

        return new Chunk(world, chunkPrimer, pos.x, pos.z);
    }

    public NBTTagCompound toNBT(NBTSerializer<T> serializer, Symbol[] symbols) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("rootNode", writeNode(node, Arrays.asList(symbols)));
        nbt.setIntArray("blocksSize", new int[]{blocks.getXSize(), blocks.getYSize(), blocks.getZSize()});
        nbt.setIntArray("blocksOff", new int[]{blocks.getXOff(), blocks.getYOff(), blocks.getZOff()});
        NBTTagList xList = new NBTTagList();
        for (int x = 0; x < blocks.getXSize(); x++) {
            NBTTagList yList = new NBTTagList();
            for (int y = 0; y < blocks.getYSize(); y++) {
                NBTTagList zList = new NBTTagList();
                for (int z = 0; z < blocks.getZSize(); z++) {
                    zList.appendTag(serializer.serialize(blocks.get(x + blocks.getXOff(), y + blocks.getYOff(), z + blocks.getZOff())));
                }
                yList.appendTag(zList);
            }
            xList.appendTag(yList);
        }
        nbt.setTag("blocksContent", xList);
        return nbt;
    }

    private NBTTagCompound writeNode(BlockNode node, List<Symbol> symbols) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("symbolIndex", symbols.indexOf(node.cube().symbol()));
        nbt.setIntArray("shapeFrom", new int[]{node.cube().shape().from().getX(), node.cube().shape().from().getY(), node.cube().shape().from().getZ()});
        nbt.setIntArray("shapeTo", new int[]{node.cube().shape().to().getX(), node.cube().shape().to().getY(), node.cube().shape().to().getZ()});
        nbt.setIntArray("shapeMeta", node.cube().metadata());
        NBTTagList children = new NBTTagList();
        for (BlockNode child : node.children()) {
            children.appendTag(writeNode(child, symbols));
        }
        if (!node.children().isEmpty()) nbt.setTag("children", children);
        return nbt;
    }

    public static <T> GenBlock<T> fromNBT(NBTTagCompound nbt, NBTSerializer<T> serializer, Symbol[] symbols) {
        BlockNode node = readNode(nbt.getCompoundTag("rootNode"), symbols);
        int[] size = nbt.getIntArray("blocksSize");
        int[] off = nbt.getIntArray("blocksOff");
        BlockHolder<T> holder = new BlockHolder<>(size[0], size[1], size[2], off[0], off[1], off[2]);
        NBTTagList xList = nbt.getTagList("blocksContent", Constants.NBT.TAG_LIST);
        for (int x = 0; x < xList.tagCount(); x++) {
            NBTTagList yList = (NBTTagList) xList.get(x);
            for (int y = 0; y < yList.tagCount(); y++) {
                NBTTagList zList = (NBTTagList) yList.get(y);
                for (int z = 0; z < zList.tagCount(); z++) {
                    holder.set(x + off[0], y + off[1], z + off[2], serializer.deserialize(zList.get(z)));
                }
            }
        }
        return new GenBlock<>(new Random(), node, holder);
    }

    private static BlockNode readNode(NBTTagCompound nbt, Symbol[] symbols) {
        int[] from = nbt.getIntArray("shapeFrom");
        int[] to = nbt.getIntArray("shapeTo");
        Shape<Cube> shape = new Shape<>(
                symbols[nbt.getInteger("symbolIndex")],
                new Cube(new Vec3i(from[0], from[1], from[2]), new Vec3i(to[0], to[1], to[2])),
                nbt.getIntArray("shapeMeta")
        );

        if (!nbt.hasKey("children")) return new BlockNode(shape, new ArrayList<>());
        else {
            NBTTagList children = nbt.getTagList("children", Constants.NBT.TAG_COMPOUND);
            BlockNode node = new BlockNode(shape, new ArrayList<>(children.tagCount()));
            for (int i = 0; i < children.tagCount(); i++) {
                node.children().add(readNode(children.getCompoundTagAt(i), symbols));
            }
            return node;
        }
    }
}
