package com.vidarin.adminspace.init;

import com.vidarin.adminspace.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<Block> BLOCKS;

    public static final Block voidTile;
    public static final Block voidErrTile;
    public static final Block concealmentBlock;

    public static final Block voidLamp;
    public static final Block voidLampOff;
    public static final Block hellBulb;

    public static final Block voidDoor;
    public static final Block voidChest;
    public static final Block musicPlayer;
    public static final Block toggleButtonOff;
    public static final Block toggleButtonOn; // I had to make two separate blocks because the blockstates didn't want to work

    public static final Block voidGlass;

    public static final Block voidCreep;
    public static final Block voidCorruption;

    public static final Block skyGround;
    public static final Block skyGround2;

    public static final Block moonBlock;
    public static final Block sunBlock;

    public static final Block corridorExposedPipes;
    public static final Block corridorTiledPipes;
    public static final Block corridorLantern;
    public static final Block corridorLight;
    public static final Block corridorMachinery;
    public static final Block corridorNetting;
    public static final Block corridorSupports;
    public static final Block corridorPillar;
    public static final Block corridorRailing;
    public static final Block corridorRailingBlock;
    public static final Block corridorSmooth;
    public static final Block corridorTracks;

    public static final Block squirmingOrganism;

    public static final Block cardReader;
    public static final Block voidMeter;
    public static final Block smallMonitor;
    public static final Block fan;
    public static final Block voidFan;

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
        hellBulb = new BlockLamp("hell_bulb");

        //Simple functional blocks
        voidDoor = new BlockModDoor("void_door", Material.IRON);
        voidChest = new BlockVoidChest("void_chest");
        musicPlayer = new BlockMusicPlayer();
        toggleButtonOff = new BlockToggleButtonOff();
        toggleButtonOn = new BlockToggleButtonOn();

        //Glass
        voidGlass = new BlockTransparent("void_glass");

        //Vine-like stuff
        voidCreep = new BlockCreep("void_creep"); //Or as RGN would call it: "blueish substance"
        voidCorruption = new BlockCreep("void_corruption");

        //Sky sector dimension
        skyGround = new BlockBase("sky_ground");
        skyGround2 = new BlockLamp("sky_ground_2");

        //Moon dimension
        moonBlock = new BlockBase("moon_block");
        sunBlock = new BlockBase("sun_block");

        //Corridor dimension
        corridorExposedPipes = new BlockBase("corridor_exposed_pipes", Material.IRON);
        corridorTiledPipes = new BlockBase("corridor_tiled_pipes", Material.IRON);
        corridorLantern = new BlockLampCustomizable("corridor_lantern", 9);
        corridorLight = new BlockLampCustomizable("corridor_light", 12);
        corridorMachinery = new BlockBase("corridor_machinery", Material.IRON);
        corridorNetting = new BlockSemiTransparent("corridor_netting");
        corridorSupports = new BlockSemiTransparent("corridor_supports");
        corridorPillar = new BlockAxisSided("corridor_pillar");
        corridorRailing = new BlockModFence("corridor_railing");
        corridorRailingBlock = new BlockBase("corridor_railing_block");
        corridorSmooth = new BlockBase("corridor_smooth");
        corridorTracks = new BlockAxisSided("corridor_tracks");

        //Void being stuff
        squirmingOrganism = new BlockDamaging("squirming_organism", 2, DamageSource.WITHER);

        //Decorations
        cardReader = new BlockSided("card_reader");
        voidMeter = new BlockSided("void_meter");
        smallMonitor = new BlockSided("small_monitor");
        fan = new BlockBase("fan");
        voidFan = new BlockBase("void_fan");

        //Terminals
        terminal = new BlockTerminal("terminal");
        mainTerminal = new BlockTerminal("main_terminal");

        //Other stuff
        trigger = new BlockTrigger("trigger", Sensitivity.MOBS);
    }
}
