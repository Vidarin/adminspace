package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.block.BlockModChest;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.BlockHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum CellTypes {
    Path(InnerBlocks.None, Connections.Any, 0, false),
    Wall(InnerBlocks.Wall, Connections.Any, 30, true),

    Chest(InnerBlocks.Chest, Connections.DeadEnd, 20, false),
    Term(InnerBlocks.Term, Connections.All, 2, false),
    WallTerm(InnerBlocks.WallTerm, Connections.ThreeWay, 1, false);

    private final InnerBlocks innerBlocks;
    private final Connections connections;
    private final int weight;
    private final boolean solid;

    CellTypes(InnerBlocks innerBlocks, Connections connections, int weight, boolean solid) {
        this.innerBlocks = innerBlocks;
        this.connections = connections;
        this.weight = weight;
        this.solid = solid;
    }

    public InnerBlocks getInnerBlocks() {
        return this.innerBlocks;
    }

    public Connections getConnections() {
        return this.connections;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public static CellTypes randomSpecial(Random rand, Connections.Connection connection, boolean solid) {
        List<CellTypes> values = new ArrayList<>(Arrays.asList(values()));
        Iterator<CellTypes> iterator = values.iterator();

        int totalWeight = 0;
        while (iterator.hasNext()) {
            CellTypes cellType = iterator.next();
            if (cellType.connections.isAllowed(connection) && cellType.solid == solid) totalWeight += cellType.getWeight();
            else iterator.remove();
        }

        if (totalWeight == 0) return solid ? Wall : Path;

        int r = rand.nextInt(totalWeight);
        int cumulative = 0;

        for (CellTypes cellType : values) {
            cumulative += cellType.getWeight();
            if (r < cumulative) {
                return cellType;
            }
        }

        return solid ? Wall : Path;
    }

    public enum InnerBlocks {
        None(
                ".........", ".........", ".........",
                (c, dir) -> Blocks.AIR.getDefaultState()
        ),
        Wall(
                "wwwwwwwww", "wwwwwwwww", "wwwwwwwww",
                (c, dir) -> BlockInit.voidTile.getDefaultState()
        ),
        Chest(
                ".........", ".........", ".......c.",
                (c, dir) -> {
                    if (c == 'c') return BlockInit.voidChest.getDefaultState().withProperty(BlockModChest.FACING, dir);
                    return Blocks.AIR.getDefaultState();
                }
        ),
        Term(
                "....w....", "....t....", "....w....",
                (c, dir) -> {
                    if (c == 'w') return BlockInit.voidTile.getDefaultState(); if (c == 't') return BlockInit.terminal.getDefaultState();
                    return Blocks.AIR.getDefaultState();
                }
        ),
        WallTerm(
                "..w..w..w", "..d..t..a", "..w..w..w",
                (c, dir) -> {
                    if (c == 'w') return BlockInit.voidTile.getDefaultState(); if (c == 't') return BlockInit.terminal.getDefaultState();
                    if (c == 'd') return BlockInit.terminalDeny.getDefaultState(); if (c == 'a') return BlockInit.terminalAccept.getDefaultState();
                    return Blocks.AIR.getDefaultState();
                }
        );

        private final BiFunction<Character, EnumFacing, IBlockState> stateGetter;
        private final Map<BlockPos, Character> blockMap = new HashMap<>();

        InnerBlocks(CharSequence t, CharSequence m, CharSequence b, BiFunction<Character, EnumFacing, IBlockState> stateGetter) {
            for (int ti = 1; ti <= 9; ti++) {
                BlockPos topPos = new BlockPos((ti - 1) / 3, 2, (ti - 1) % 3);
                blockMap.put(topPos, t.charAt(ti - 1));
            }
            for (int mi = 1; mi <= 9; mi++) {
                BlockPos middlePos = new BlockPos((mi - 1) / 3, 1, (mi - 1) % 3);
                blockMap.put(middlePos, m.charAt(mi - 1));
            }
            for (int bi = 1; bi <= 9; bi++) {
                BlockPos bottomPos = new BlockPos((bi - 1) / 3, 0, (bi - 1) % 3);
                blockMap.put(bottomPos, b.charAt(bi - 1));
            }
            this.stateGetter = stateGetter;
        }

        public BlockHolder<IBlockState> getBlocks(EnumFacing facing) {
            if (facing.getAxis().isVertical()) throw new IllegalArgumentException("'facing' cannot be vertical");
            BlockHolder<IBlockState> holder = new BlockHolder<>(3, 3, 3);
            blockMap.forEach((pos, character) -> holder.set(pos.getX(), pos.getY(), pos.getZ(), stateGetter.apply(character, facing)));
            holder.rotate(facing);
            return holder;
        }

    }

    public enum Connections {
        Any(c -> true),
        DeadEnd(c -> c.numConnections() == 1),
        Hallway(c -> ((c.north && c.south) || (c.west && c.east)) && c.numConnections() == 2),
        Turn(c -> !((c.north && c.south) || (c.west && c.east)) && c.numConnections() == 2),
        ThreeWay(c -> c.numConnections() == 3),
        All(c -> c.numConnections() == 4);

        private final Predicate<Connection> allowed;

        Connections(Predicate<Connection> allowed) {
            this.allowed = allowed;
        }

        public boolean isAllowed(Connection connection) {
            return this.allowed.test(connection);
        }

        public static class Connection {
            public final boolean north, south, west, east;

            public final List<Character> trueConnections = new ArrayList<>();
            public final List<Character> falseConnections = new ArrayList<>();

            public Connection(boolean north, boolean south, boolean west, boolean east) {
                this.north = north;
                this.south = south;
                this.west = west;
                this.east = east;

                if (north) trueConnections.add('n'); else falseConnections.add('n');
                if (south) trueConnections.add('s'); else falseConnections.add('s');
                if (west) trueConnections.add('w'); else falseConnections.add('w');
                if (east) trueConnections.add('e'); else falseConnections.add('e');
            }

            public int numConnections() {
                return trueConnections.size();
            }

            public EnumFacing getDirection(Random random) {
                if (numConnections() == 0 || numConnections() == 4) return EnumFacing.EAST;
                else if (numConnections() == 1) return getDirection(trueConnections.get(0));
                else if (numConnections() == 2) return getDirection(trueConnections.get(random.nextInt(2)));
                else if (numConnections() == 3) return getDirection(falseConnections.get(0)).getOpposite();
                else throw new IllegalStateException("Invalid amount of connections: " + numConnections());
            }

            private EnumFacing getDirection(char c) {
                if (c == 'n') return EnumFacing.NORTH;
                else if (c == 's') return EnumFacing.SOUTH;
                else if (c == 'w') return EnumFacing.WEST;
                else if (c == 'e') return EnumFacing.EAST;
                else throw new IllegalArgumentException("Invalid direction key: " + c);
            }
        }
    }
}
