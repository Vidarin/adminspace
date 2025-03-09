package com.vidarin.adminspace.init;

import com.vidarin.adminspace.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<Block> BLOCKS;

    public static final Block voidTile;
    public static final Block voidErrTile;
    public static final Block voidWall;
    public static final Block voidStairs;
    public static final Block voidErrStairs;
    public static final Block concealmentBlock;

    public static final Block voidLamp;
    public static final Block voidLampOff;
    public static final Block hellBulb;

    public static final Block voidDoor;
    public static final Block voidChest;
    public static final Block musicPlayer;
    public static final Block toggleButtonOff;
    public static final Block toggleButtonOn; // I had to make two separate blocks because the blockstates didn't want to work
    public static final Block voidLever;

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

    public static final Block noiseGemOre;
    public static final Block rainbowOre;

    public static final Block frozenLeaves;
    public static final Block dataDaisy;

    public static final Block cardReader;
    public static final Block voidGauge;
    public static final Block smallMonitor;
    public static final Block voidGaugeAll;
    public static final Block smallMonitorAll;
    public static final Block fan;
    public static final Block voidFan;

    public static final Block minesweeper0;
    public static final Block minesweeper1;
    public static final Block minesweeper2;
    public static final Block minesweeper3;
    public static final Block minesweeper4;
    public static final Block minesweeper5;
    public static final Block minesweeper6;
    public static final Block minesweeper7;
    public static final Block minesweeper8;
    public static final Block minesweeperFlag;
    public static final Block minesweeperMine;
    public static final Block minesweeperMineCritical;
    public static final Block minesweeperButton;

    public static final Block terminal;
    public static final Block mainTerminal;
    public static final Block terminalAccept;
    public static final Block terminalDeny;

    public static final Block trigger;
    public static final Block magicalTeleporterDeltaQuest;
    public static final Block magicalTeleporterSkySector;

    static {
        BLOCKS = new ArrayList<>();

        //Basic blocks
        voidTile = new BlockBase("void_tile", Material.ROCK, CreativeTabs.BUILDING_BLOCKS);
        voidErrTile = new BlockBase("void_err_tile");
        voidWall = new BlockModWall("void_wall", Material.ROCK, CreativeTabs.DECORATIONS);
        voidStairs = new BlockModStairs("void_stairs", CreativeTabs.DECORATIONS, voidTile);
        voidErrStairs = new BlockModStairs("void_err_stairs", CreativeTabs.DECORATIONS, voidErrTile);
        concealmentBlock = new BlockConcealmentBlock();

        //Lamps
        voidLamp = new BlockLamp("void_lamp");
        voidLampOff = new BlockBase("void_lamp_off");
        hellBulb = new BlockLamp("hell_bulb");

        //Simple functional blocks
        voidDoor = new BlockModDoor("void_door", Material.IRON, null);
        voidChest = new BlockVoidChest("void_chest");
        musicPlayer = new BlockMusicPlayer();
        toggleButtonOff = new BlockToggleButtonOff();
        toggleButtonOn = new BlockToggleButtonOn();
        voidLever = new BlockModLever("void_lever", CreativeTabs.REDSTONE);

        //Transparent blocks
        voidGlass = new BlockTransparent("void_glass");

        //Vine-like stuff
        voidCreep = new BlockCreep("void_creep"); //Or as RGN would call it: "blueish substance"
        voidCorruption = new BlockCreep("void_corruption");

        //Sky sector dimension
        skyGround = new BlockBase("sky_ground");
        skyGround2 = new BlockLamp("sky_ground_2");
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

        //Ores
        noiseGemOre = new BlockCustomDrop("noise_gem_ore", Material.ROCK, CreativeTabs.BUILDING_BLOCKS, ItemInit.noiseGem, 1, 1);
        rainbowOre = new BlockCustomDrop("rainbow_ore", ItemInit.rainbowGem, 1, 1);

        //Plants
        frozenLeaves = new BlockModLeaves("frozen_leaves");
        dataDaisy = new BlockDataDaisy();

        //Decorations
        cardReader = new BlockSided("card_reader");
        voidGauge = new BlockSided("void_gauge");
        smallMonitor = new BlockSided("small_monitor");
        voidGaugeAll = new BlockBase("void_gauge_all");
        smallMonitorAll = new BlockBase("small_monitor_all");
        fan = new BlockBase("fan");
        voidFan = new BlockBase("void_fan");

        //Minesweeper
        minesweeper0 = new BlockMinesweeperTile("minesweeper_0");
        minesweeper1 = new BlockMinesweeperTile("minesweeper_1");
        minesweeper2 = new BlockMinesweeperTile("minesweeper_2");
        minesweeper3 = new BlockMinesweeperTile("minesweeper_3");
        minesweeper4 = new BlockMinesweeperTile("minesweeper_4");
        minesweeper5 = new BlockMinesweeperTile("minesweeper_5");
        minesweeper6 = new BlockMinesweeperTile("minesweeper_6");
        minesweeper7 = new BlockMinesweeperTile("minesweeper_7");
        minesweeper8 = new BlockMinesweeperTile("minesweeper_8");
        minesweeperFlag = new BlockMinesweeperTile("minesweeper_flag");
        minesweeperMine = new BlockMinesweeperTile("minesweeper_mine");
        minesweeperMineCritical = new BlockMinesweeperTile("minesweeper_mine_critical");
        minesweeperButton = new BlockMinesweeperButton(); // TODO: fix the minesweeper stuff

        //Terminals
        terminal = new BlockTerminal("terminal");
        mainTerminal = new BlockTerminal("main_terminal");
        terminalAccept = new BlockTerminalAccept();
        terminalDeny = new BlockTerminalDeny();

        //Other stuff
        trigger = new BlockTrigger("trigger", Sensitivity.MOBS);
        magicalTeleporterDeltaQuest = new BlockTeleporter("teleporter_dq", 100);
        magicalTeleporterSkySector = new BlockTeleporter("teleporter_ss", 20);
    }
}
