package com.vidarin.adminspace.worldgen.genblock;

import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.WildcardMap;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import com.vidarin.adminspace.util.CubePos;
import com.vidarin.adminspace.util.NBTSerializer;
import com.vidarin.adminspace.worldgen.grammar.RuleSet;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
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
import java.util.concurrent.atomic.AtomicInteger;

public class GenBlock<T> {
    private Random rand;

    private final BlockNode node;
    private final BlockHolder<T> blocks;

    private final WildcardMap<String> globals;

    public GenBlock(Shape<Cube> baseShape, Random rand) {
        this(
                rand,
                new BlockNode(baseShape, new ArrayList<>()),
                new BlockHolder<>(
                        baseShape.shape().sizeX(), baseShape.shape().sizeY() + 1, baseShape.shape().sizeZ(),
                        baseShape.shape().from().getX(), baseShape.shape().from().getY(), baseShape.shape().from().getZ()
                ),
                new WildcardMap<>(NBTSerializer.STRING)
        );
    }

    private GenBlock(Random rand, BlockNode node, BlockHolder<T> blocks, WildcardMap<String> globals) {
        this.rand = rand;
        this.node = node;
        this.blocks = blocks;
        this.globals = globals;
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

            Iterable<Shape<Pair<BlockHolder<T>, Cube>>> shapes = rules.get(child.cube(), this.rand, globals);

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
                BlockHolder<T> newBlocks = shape.shape().getLeft();
                Cube cube = shape.shape().getRight();

                Adminspace.LOGGER.info(shape.symbol());

                if (newBlocks != null) this.blocks.copyFrom(newBlocks, cube.from().getX(), cube.from().getY(), cube.from().getZ());

                Shape<Cube> cubeShape = new Shape<>(shape.symbol(), cube, shape.meta());
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
        nbt.setTag("globals", globals.toNBT());
        nbt.setTag("rootNode", writeNode(node, Arrays.asList(symbols)));
        nbt.setIntArray("blocksSize", new int[]{blocks.getXSize(), blocks.getYSize(), blocks.getZSize()});
        nbt.setIntArray("blocksOff", new int[]{blocks.getXOff(), blocks.getYOff(), blocks.getZOff()});

        List<T> palette = new ArrayList<>();
        Object2ByteMap<T> paletteIndices = new Object2ByteOpenHashMap<>();
        byte[] indices = new byte[blocks.getXSize() * blocks.getYSize() * blocks.getZSize()];
        AtomicInteger idx = new AtomicInteger();

        blocks.forEach((x, y, z, t) -> {
            byte i = paletteIndices.computeIfAbsent(t, v -> {
                if (palette.size() == 0xFF) throw new IllegalStateException("Palette too large, max 255 different elements are allowed");
                palette.add(v);
                return (byte) (palette.size() - 1);
            });
            indices[idx.getAndIncrement()] = i;
        });

        NBTTagList paletteTag = new NBTTagList();
        for (T value : palette) {
            paletteTag.appendTag(serializer.serialize(value));
        }
        nbt.setTag("blocksPalette", paletteTag);

        int size = (indices.length / 4);
        int left = indices.length % 4;

        int[] arr = new int[size + (left == 0 ? 0 : 1)];
        for (int i = 0; i < size; i++) {
            byte b1 = indices[i << 2];
            byte b2 = indices[(i << 2) + 1];
            byte b3 = indices[(i << 2) + 2];
            byte b4 = indices[(i << 2) + 3];
            arr[i] = ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);
        }
        if (left != 0) {
            byte b1 = indices[size << 2];
            byte b2 = left >= 2 ? indices[(size << 2) + 1] : 0;
            byte b3 = left == 3 ? indices[(size << 2) + 2] : 0;
            arr[size] = ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8);
        }
        nbt.setIntArray("blocksContent", arr);
        Adminspace.LOGGER.debug("Successfully wrote GenBlock content in {} bytes", arr.length * 4);
        return nbt;
    }

    private NBTTagCompound writeNode(BlockNode node, List<Symbol> symbols) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("symbolIndex", symbols.indexOf(node.cube().symbol()));
        nbt.setIntArray("shapeFrom", new int[]{node.cube().shape().from().getX(), node.cube().shape().from().getY(), node.cube().shape().from().getZ()});
        nbt.setIntArray("shapeTo", new int[]{node.cube().shape().to().getX(), node.cube().shape().to().getY(), node.cube().shape().to().getZ()});
        nbt.setIntArray("shapeMeta", node.cube().meta());
        if (!node.children().isEmpty()) {
            NBTTagList children = new NBTTagList();
            for (BlockNode child : node.children()) {
                children.appendTag(writeNode(child, symbols));
            }
            nbt.setTag("children", children);
        }
        return nbt;
    }

    public static <T> GenBlock<T> fromNBT(NBTTagCompound nbt, NBTSerializer<T> serializer, Symbol[] symbols) {
        WildcardMap<String> globals = WildcardMap.fromNBT(NBTSerializer.STRING, nbt.getTagList("globals", Constants.NBT.TAG_COMPOUND));
        BlockNode node = readNode(nbt.getCompoundTag("rootNode"), symbols);
        int[] size = nbt.getIntArray("blocksSize");
        int[] off = nbt.getIntArray("blocksOff");
        BlockHolder<T> holder = new BlockHolder<>(size[0], size[1], size[2], off[0], off[1], off[2]);

        NBTTagList paletteTag = nbt.getTagList("blocksPalette", Constants.NBT.TAG_COMPOUND);
        List<T> palette = new ArrayList<>(paletteTag.tagCount());
        for (int i = 0; i < paletteTag.tagCount(); i++) {
            palette.add(serializer.deserialize(paletteTag.getCompoundTagAt(i)));
        }

        int[] arr = nbt.getIntArray("blocksContent");

        int i = 0;
        for (int x = 0; x < size[0]; x++) {
            for (int y = 0; y < size[1]; y++) {
                for (int z = 0; z < size[2]; z++) {
                    int left = i % 4;
                    int shift = switch (left) {
                        case 0 -> 24;
                        case 1 -> 16;
                        case 2 -> 8;
                        default -> 0;
                    };
                    byte paletteIndex = (byte) ((arr[i / 4] >> shift) & 0xFF);
                    holder.set(x + off[0], y + off[1], z + off[2], palette.get(paletteIndex));
                    i++;
                }
            }
        }

        Adminspace.LOGGER.debug("Successfully read GenBlock content in {} bytes", arr.length * 4);

        return new GenBlock<>(new Random(), node, holder, globals);
    }

    private static BlockNode readNode(NBTTagCompound nbt, Symbol[] symbols) {
        int[] from = nbt.getIntArray("shapeFrom");
        int[] to = nbt.getIntArray("shapeTo");
        Shape<Cube> shape = new Shape<>(
                symbols[nbt.getInteger("symbolIndex")],
                new Cube(new CubePos(from[0], from[1], from[2]), new CubePos(to[0], to[1], to[2])),
                nbt.getIntArray("shapeMeta")
        );

        if (!nbt.hasKey("children")) return new BlockNode(shape, new ArrayList<>());
        else {
            NBTTagList children = nbt.getTagList("children", Constants.NBT.TAG_COMPOUND);
            if (children.isEmpty()) return new BlockNode(shape, new ArrayList<>());
            BlockNode node = new BlockNode(shape, new ArrayList<>(children.tagCount()));
            for (int i = 0; i < children.tagCount(); i++) {
                node.children().add(readNode(children.getCompoundTagAt(i), symbols));
            }
            return node;
        }
    }
}
