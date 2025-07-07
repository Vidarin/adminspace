package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.block.BlockModDoor;
import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum CubeTypes { //Here north is positive X and west is positive Z because I refuse to think north is negative Z
    Empty(Blocks.AIR.getDefaultState(), DetailTypes.None, Connections.NS, "add_doors", 100),
    EmptyFlipped(Blocks.AIR.getDefaultState(), DetailTypes.None, Connections.WE, "add_doors", 100),
    Empty1Way(Blocks.AIR.getDefaultState(), DetailTypes.None, Connections.OneWay, "add_doors", 60),
    Empty2Way(Blocks.AIR.getDefaultState(), DetailTypes.None, Connections.TwoWay, "add_doors", 70),
    Empty3Way(Blocks.AIR.getDefaultState(), DetailTypes.None, Connections.ThreeWay, "add_doors", 60),
    Empty4Way(Blocks.AIR.getDefaultState(), DetailTypes.None, Connections.FourWay, "add_doors", 40),

    Filled(BlockInit.voidTile.getDefaultState(), DetailTypes.None, Connections.None, "", 90),
    Lamp(BlockInit.voidTile.getDefaultState(), DetailTypes.Lamp, Connections.None, "", 50),
    FullLamp(BlockInit.voidLamp.getDefaultState(), DetailTypes.None, Connections.None, "", 3),
    ErrFilled(BlockInit.voidErrTile.getDefaultState(), DetailTypes.None, Connections.None, "", 20),
    ErrLamp(BlockInit.voidErrTile.getDefaultState(), DetailTypes.Lamp, Connections.None, "", 10),

    Door(BlockInit.voidTile.getDefaultState(), DetailTypes.None, Connections.None, "door", 0),
    Window(Blocks.AIR.getDefaultState(), DetailTypes.Window, Connections.NS, "", 3),
    WindowFlipped(Blocks.AIR.getDefaultState(), DetailTypes.WindowFlipped, Connections.WE, "", 3),
    SmallWindow(Blocks.AIR.getDefaultState(), DetailTypes.SmallWindow, Connections.NS, "", 5),
    SmallWindowFlipped(Blocks.AIR.getDefaultState(), DetailTypes.SmallWindowFlipped, Connections.WE, "", 5),
    Monitor(Blocks.AIR.getDefaultState(), DetailTypes.Monitor, Connections.TwoWay, "", 3),
    Terminal(Blocks.AIR.getDefaultState(), DetailTypes.Terminal, Connections.OneWay, "", 1),
    Doors(Blocks.AIR.getDefaultState(), DetailTypes.Doors, Connections.WE, "", 2),

    LadderBottom(Blocks.AIR.getDefaultState(), DetailTypes.Ladder, Connections.OneWay, "", 0),
    LadderTop(Blocks.AIR.getDefaultState(), DetailTypes.LadderTop, Connections.FourWay, "", 0),

    DisposalEntrance(Blocks.AIR.getDefaultState(), DetailTypes.DisposalEntrance, Connections.OneWay, "tunnel_to_disposal", 1),
    VoidObservationDeck(Blocks.AIR.getDefaultState(), DetailTypes.VoidObservationDeck, Connections.OneWay, "glass_floor", 20);

    private final IBlockState mainBlock;

    private final DetailTypes detail;

    private final Connections connection;

    private final String special;

    private final int weight;

    CubeTypes(IBlockState mainBlock, DetailTypes detail, Connections connection, String special, int weight) {
        this.mainBlock = mainBlock;
        this.detail = detail;
        this.connection = connection;
        this.special = special;
        this.weight = weight;
    }

    public boolean isDown() {
        return connection.isDown();
    }

    public boolean isUp() {
        return connection.isUp();
    }

    public boolean isNorth() {
        return connection.isNorth();
    }

    public boolean isSouth() {
        return connection.isSouth();
    }

    public boolean isWest() {
        return connection.isWest();
    }

    public boolean isEast() {
        return connection.isEast();
    }

    public DetailTypes getDetail() {
        return detail;
    }

    public IBlockState getMainBlock() {
        return mainBlock;
    }

    public String getSpecial() {
        return special;
    }

    public int getWeight() {
        return weight;
    }

    public enum DetailTypes {
        None(
                ".........",
                ".........",
                "........."
        ),
        Lamp(
                "....*....",
                ".*..*..*.",
                "....*...."
        ),
        Ladder(
                "...#:....",
                "...#*....",
                "...#:...."
        ),
        LadderTop(
                "...#:....",
                "...#*....",
                ":*:#:*:*:"
        ),
        DisposalEntrance(
                ".........",
                ".........",
                "××××.××××"
        ),
        VoidObservationDeck(
                ".:.:::.:.",
                "....%....",
                "....:...."
        ),
        Monitor(
                "....:....",
                "....-....",
                "....:...."
        ),
        Terminal(
                ".........",
                "....=....",
                "....:...."
        ),
        Window(
                "...000...",
                "...000...",
                "...000..."
        ),
        WindowFlipped(
                ".0..0..0.",
                ".0..0..0.",
                ".0..0..0."
        ),
        SmallWindow(
                "...:::...",
                "...:0:...",
                "...:::..."
        ),
        SmallWindowFlipped(
                ".:..:..:.",
                ".:..0..:.",
                ".:..:..:."
        ),
        Doors(
                ".........",
                "D..D..D..",
                "d..d..d.."
        );


        private final Map<BlockPos, IBlockState> topBlockMap = new HashMap<>();
        private final Map<BlockPos, IBlockState> middleBlockMap = new HashMap<>();
        private final Map<BlockPos, IBlockState> bottomBlockMap = new HashMap<>();

        DetailTypes(CharSequence t, CharSequence m, CharSequence b) {
            for (int ti = 1; ti <= 9; ti++) {
                BlockPos topPos = new BlockPos((ti - 1) / 3, 2, (ti - 1) % 3);
                IBlockState topBlock = getBlock(t.charAt(ti - 1));
                topBlockMap.put(topPos, topBlock);
            }
            for (int mi = 1; mi <= 9; mi++) {
                BlockPos middlePos = new BlockPos((mi - 1) / 3, 1, (mi - 1) % 3);
                IBlockState middleBlock = getBlock(m.charAt(mi - 1));
                middleBlockMap.put(middlePos, middleBlock);
            }
            for (int bi = 1; bi <= 9; bi++) {
                BlockPos bottomPos = new BlockPos((bi - 1) / 3, 0, (bi - 1) % 3);
                IBlockState bottomBlock = getBlock(b.charAt(bi - 1));
                bottomBlockMap.put(bottomPos, bottomBlock);
            }
        }

        public IBlockState getBlock(char c) {
            switch (c) {
                case '*':
                    return BlockInit.voidLamp.getDefaultState();
                case '×':
                    return BlockInit.voidLampOff.getDefaultState();
                case ':':
                    return BlockInit.voidTile.getDefaultState();
                case ';':
                    return BlockInit.voidErrTile.getDefaultState();
                case '0':
                    return BlockInit.voidGlass.getDefaultState();
                case '#':
                    return Blocks.LADDER.getDefaultState();
                case '%':
                    return BlockInit.voidGaugeAll.getDefaultState();
                case '-':
                    return BlockInit.smallMonitorAll.getDefaultState();
                case '=':
                    return BlockInit.terminal.getDefaultState();
                case 'd':
                    return BlockInit.voidDoor.getDefaultState().withProperty(BlockModDoor.HALF, BlockDoor.EnumDoorHalf.LOWER);
                case 'D':
                    return BlockInit.voidDoor.getDefaultState().withProperty(BlockModDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
            }
            return Blocks.AIR.getDefaultState(); // Air means nothing will change ('.' represents air)
        }

        public IBlockState getTopBlockFromBlockPos(BlockPos pos) {
            return topBlockMap.getOrDefault(pos, Blocks.AIR.getDefaultState());
        }

        public IBlockState getMiddleBlockFromBlockPos(BlockPos pos) {
            return middleBlockMap.getOrDefault(pos, Blocks.AIR.getDefaultState());
        }

        public IBlockState getBottomBlockFromBlockPos(BlockPos pos) {
            return bottomBlockMap.getOrDefault(pos, Blocks.AIR.getDefaultState());
        }
    }

    public enum Connections {
        None(false, false, false, false, false, false, 0),
        NS(true, true, false, false, false, false, 0),
        WE(false, false, true, true, false, false, 0),
        OneWay(false, false, false, false, false, false, 1),
        TwoWay(false, false, false, false, false, false, 2),
        ThreeWay(false, false, false, false, false, false, 3),
        FourWay(true, true, true, true, false, false, 0);

        private final boolean north;
        private final boolean south;
        private final boolean west;
        private final boolean east;
        private final boolean up;
        private final boolean down;

        Connections(boolean north, boolean south, boolean west, boolean east, boolean up, boolean down, int random) {
            boolean n = north;
            boolean s = south;
            boolean w = west;
            boolean e = east;
            this.up = up;
            this.down = down;

            if (random != 0) {
                Random rng = new Random();
                for (int i = 0; i < random; i++) {
                    byte r = (byte) rng.nextInt(4);
                    switch (r) {
                        case 0:
                            n = true;
                            continue;
                        case 1:
                            s = true;
                            continue;
                        case 2:
                            w = true;
                            continue;
                        case 3:
                            e = true;
                    }
                }
            }
            this.north = n;
            this.south = s;
            this.west = w;
            this.east = e;
        }

        public boolean isDown() {
            return down;
        }

        public boolean isUp() {
            return up;
        }

        public boolean isNorth() {
            return north;
        }

        public boolean isSouth() {
            return south;
        }

        public boolean isWest() {
            return west;
        }

        public boolean isEast() {
            return east;
        }
    }
}
