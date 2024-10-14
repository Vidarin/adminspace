package com.vidarin.adminspace.init;

import com.vidarin.adminspace.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<Block> BLOCKS;

    public static final Block voidTile;
    public static final Block voidErrTile;
    public static final Block concealmentBlock;

    public static final Block voidLamp;
    public static final Block voidLampOff;

    public static final Block voidDoor;
    public static final Block voidChest;
    public static final Block musicPlayer;

    public static final Block voidGlass;

    public static final Block voidCreep;
    public static final Block voidCorruption;

    public static final Block skyGround;
    public static final Block skyGround2;

    public static final Block cardReader;
    public static final Block voidMeter;
    public static final Block smallMonitor;

    public static final Block terminal;
    public static final Block mainTerminal;

    public static final Block trigger;

    static {
        BLOCKS = new ArrayList<>();

        //Basic blocks
        voidTile = new BlockBase("void_tile");
        voidErrTile = new BlockBase("void_err_tile");
        concealmentBlock = new BlockBase("concealment_block");

        //Lamps
        voidLamp = new BlockLamp("void_lamp");
        voidLampOff = new BlockBase("void_lamp_off");

        //Simple functional blocks
        voidDoor = new BlockModDoor("void_door", Material.IRON);
        voidChest = new BlockVoidChest("void_chest");
        musicPlayer = new BlockMusicPlayer();

        //Glass
        voidGlass = new BlockTransparent("void_glass");

        //Vine-like stuff
        voidCreep = new BlockCreep("void_creep"); //Or as RGN would call it: "blueish substance"
        voidCorruption = new BlockCreep("void_corruption");

        //Sky sector dimension
        skyGround = new BlockBase("sky_ground");
        skyGround2 = new BlockLamp("sky_ground_2");

        //Decorations
        cardReader = new BlockSided("card_reader");
        voidMeter = new BlockSided("void_meter");
        smallMonitor = new BlockSided("small_monitor");

        //Terminals
        terminal = new BlockTerminal("terminal");
        mainTerminal = new BlockTerminal("main_terminal");

        //Other stuff
        trigger = new BlockTrigger("trigger", Sensitivity.MOBS);
    }
}
