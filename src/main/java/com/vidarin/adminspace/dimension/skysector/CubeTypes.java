package com.vidarin.adminspace.dimension.skysector;

import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum CubeTypes { //Here north is positive X and west is positive Z because I refuse to think north is negative Z
    Empty(Blocks.AIR.getDefaultState(), DetailTypes.None, true, true, false, false, false, false, 0, "add_doors", 50),
    EmptyFlipped(Blocks.AIR.getDefaultState(), DetailTypes.None, false, false, true, true, false, false, 0, "add_doors", 50),
    Empty2Way(Blocks.AIR.getDefaultState(), DetailTypes.None, false, false, false, false, false, false, 2, "add_doors", 40),
    Empty3Way(Blocks.AIR.getDefaultState(), DetailTypes.None, false, false, false, false, false, false, 3, "add_doors", 30),
    Empty4Way(Blocks.AIR.getDefaultState(), DetailTypes.None, true, true, true, true, false, false, 0, "add_doors", 10),
    EmptyDeadEnd(Blocks.AIR.getDefaultState(), DetailTypes.None, false, false, false, false, false, false, 1, "add_doors", 100),

    Filled(BlockInit.voidTile.getDefaultState(), DetailTypes.None, false, false, false, false, false, false, 0, "", 100),
    Lamp(BlockInit.voidTile.getDefaultState(), DetailTypes.Lamp, false, false, false, false, false, false, 0, "", 50),
    FullLamp(BlockInit.voidLamp.getDefaultState(), DetailTypes.None, false, false, false, false, false, false, 0, "", 5),
    ErrFilled(BlockInit.voidErrTile.getDefaultState(), DetailTypes.None, false, false, false, false, false, false, 0, "", 20),
    ErrLamp(BlockInit.voidErrTile.getDefaultState(), DetailTypes.Lamp, false, false, false, false, false, false, 0, "", 10),

    Door(Blocks.AIR.getDefaultState(), DetailTypes.Door, true, true, false, false, false, false, 0, "", 30),
    DoorFlipped(Blocks.AIR.getDefaultState(), DetailTypes.DoorFlipped, false, false, true, true, false, false, 0, "", 30),
    Window(Blocks.AIR.getDefaultState(), DetailTypes.Window, true, true, false, false, false, false, 0, "", 10),
    WindowFlipped(Blocks.AIR.getDefaultState(), DetailTypes.WindowFlipped, false, false, true, true, false, false, 0, "", 10),
    SmallWindow(Blocks.AIR.getDefaultState(), DetailTypes.SmallWindow, true, true, false, false, false, false, 0, "", 5),
    SmallWindowFlipped(Blocks.AIR.getDefaultState(), DetailTypes.SmallWindowFlipped, false, false, true, true, false, false, 0, "", 5),
    Monitor(Blocks.AIR.getDefaultState(), DetailTypes.Monitor, false, false, false, false, false, false, 2, "", 5),
    Terminal(Blocks.AIR.getDefaultState(), DetailTypes.Terminal, false, false, false, false, false, false, 1, "", 1),
    Stairs(Blocks.AIR.getDefaultState(), DetailTypes.Stairs, false, false, false, false, true, false, 1, "", 3),


    LadderBottom(Blocks.AIR.getDefaultState(), DetailTypes.Ladder, false, false, false, false, true, false, 1, "", 5),
    Ladder(Blocks.AIR.getDefaultState(), DetailTypes.Ladder, false, false, false, false, true, true, 0, "", 100),
    LadderTop(Blocks.AIR.getDefaultState(), DetailTypes.LadderTop, false, false, false, false, false, true, 1, "", 100),

    DisposalEntrance(Blocks.AIR.getDefaultState(), DetailTypes.DisposalEntrance, false, false, false, false, false, false, 1, "tunnel_to_disposal", 1),
    VoidObservationDeck(Blocks.AIR.getDefaultState(), DetailTypes.VoidObservationDeck, false, false, false, false, false, false, 1, "glass_floor", 20);

    private final IBlockState mainBlock;
    private final DetailTypes detail;

    private final boolean north;
    private final boolean south;
    private final boolean west;
    private final boolean east;
    private final boolean up;
    private final boolean down;

    private final String special;

    private final int weight;

    CubeTypes(IBlockState mainBlock, DetailTypes detail, boolean north, boolean south, boolean west, boolean east, boolean up, boolean down, int random, String special, int weight) {
        boolean n = north;
        boolean s = south;
        boolean w = west;
        boolean e = east;
        this.mainBlock = mainBlock;
        this.detail = detail;
        this.special = special;
        this.weight = weight;

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
        Door(
                ":::::::::",
                ":::...:::",
                ":::...:::"
        ),
        DoorFlipped(
                ":::::::::",
                ":.::.::.:",
                ":.::.::.:"
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
        Stairs(
                "..::....:",
                ".:..:..:.",
                ":....::.."
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
}
