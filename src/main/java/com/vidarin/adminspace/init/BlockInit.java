package com.vidarin.adminspace.init;

import com.vidarin.adminspace.block.*;
import com.vidarin.adminspace.block.special.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.SoundType;
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
    public static final Block voidSlab;
    public static final Block voidErrStairs;
    public static final Block concealmentBlock;

    public static final Block voidLamp;
    public static final Block voidLampOff;
    public static final Block hellBulb;

    public static final Block voidDoor;
    public static final Block voidChest;
    public static final Block musicPlayer;
    public static final Block toggleButton;
    public static final Block voidLever;

    public static final Block voidGlass;

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
    public static final Block voidBeingRock;

    public static final Block noiseGemOre;
    public static final Block rainbowOre;

    public static final Block frozenLeaves;

    public static final Block dataDaisy;
    public static final Block dataBlock;
    public static final Block starBlock;

    public static final Block creeperHeart;

    public static final Block cardReader;
    public static final Block voidGauge;
    public static final Block smallMonitor;
    public static final Block voidGaugeAll;
    public static final Block smallMonitorAll;
    public static final Block voidFan;

    public static final Block fan;
    public static final Block adminColumn;
    public static final Block adminTech;
    public static final Block adminspaceGlass;
    public static final Block adminspaceTerminal;
    public static final Block adminspaceTile;
    public static final Block adminspaceWall;
    public static final Block adminspaceCatwalk;
    public static final Block keySlotter;
    public static final Block mojangBlock;
    public static final Block serverContainer;
    public static final Block monitor;

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
    public static final Block magicalTeleporterBeyond;
    public static final Block testBlockVisibility;
    public static final Block testBlockGenBlock;
    public static final Block compactingStructureBlock;

    static {
        BLOCKS = new ArrayList<>();

        //Basic blocks
        voidTile = new BlockBase("void_tile", Material.ROCK, CreativeTabs.BUILDING_BLOCKS, SoundType.ANVIL).setHardness(5.5f).setResistance(8.0f);
        voidErrTile = new BlockBase("void_err_tile", Material.ROCK, null, SoundType.ANVIL).setHardness(60.0f).setResistance(2000.0f);
        voidWall = new BlockModWall("void_wall", Material.ROCK, CreativeTabs.DECORATIONS).setHardness(5.5f).setResistance(8.0f);
        voidStairs = new BlockModStairs("void_stairs", CreativeTabs.DECORATIONS, voidTile).setHardness(5.5f).setResistance(8.0f);
        voidSlab = BlockModSlab.createSlabSet("void_slab", CreativeTabs.DECORATIONS, Material.ROCK, SoundType.ANVIL, 5.5f, 8.0f);
        voidErrStairs = new BlockModStairs("void_err_stairs", CreativeTabs.DECORATIONS, voidErrTile).setHardness(60.0f).setResistance(2000.0f);
        voidLamp = new BlockLamp("void_lamp", 15).setHardness(6.0f).setResistance(11.0f);
        voidLampOff = new BlockBase("void_lamp_off").setHardness(6.0f).setResistance(11.0f);
        hellBulb = new BlockLamp("hell_bulb", 12).setHardness(0.4f).setResistance(1.0f);
        concealmentBlock = new BlockBase("concealment_block");

        //Simple functional blocks
        voidDoor = new BlockModDoor("void_door", Material.IRON, CreativeTabs.REDSTONE).setHardness(5.5f).setResistance(8.0f);
        voidChest = new BlockModChest("void_chest").setHardness(5.5f).setResistance(8.0f);
        musicPlayer = new BlockMusicPlayer().setHardness(5.5f).setResistance(8.0f);
        toggleButton = new BlockToggleButton().setHardness(5.5f).setResistance(8.0f);
        voidLever = new BlockModLever("void_lever", CreativeTabs.REDSTONE);

        //Transparent blocks
        voidGlass = new BlockTransparent("void_glass").setHardness(6.0f).setResistance(11.0f);

        //Vine-like stuff
        voidCorruption = new BlockModVine("void_corruption").setHardness(1.0f).setResistance(2.0f);

        //Sky sector dimension
        skyGround = new BlockColor("sky_ground", Material.ROCK, null, 0x000000, 0).setHardness(5.0f).setResistance(10.0f);
        skyGround2 = new BlockColor("sky_ground_2", Material.ROCK, null, 0xFF0000, 15).setHardness(5.0f).setResistance(10.0f);
        moonBlock = new BlockBase("moon_block").setHardness(6.5f).setResistance(12.0f);
        sunBlock = new BlockBase("sun_block").setHardness(15.0f).setResistance(30.0f);

        //Corridor dimension
        corridorExposedPipes = new BlockBase("corridor_exposed_pipes", Material.IRON).setHardness(8.0f).setResistance(40.0f);
        corridorTiledPipes = new BlockBase("corridor_tiled_pipes", Material.IRON).setHardness(8.5f).setResistance(45.0f);
        corridorLantern = new BlockLamp("corridor_lantern", 9).setHardness(7.0f).setResistance(35.0f);
        corridorLight = new BlockLamp("corridor_light", 12).setHardness(6.7f).setResistance(30.0f);
        corridorMachinery = new BlockBase("corridor_machinery", Material.IRON).setHardness(9.0f).setResistance(50.0f);
        corridorNetting = new BlockTransparent("corridor_netting").alwaysRenderSides().setHardness(6.5f).setResistance(40.0f);
        corridorSupports = new BlockTransparent("corridor_supports").alwaysRenderSides().setHardness(6.5f).setResistance(40.0f);
        corridorPillar = new BlockColumn("corridor_pillar").setHardness(8.5f).setResistance(45.0f);
        corridorRailing = new BlockModFence("corridor_railing").setHardness(8.5f).setResistance(45.0f);
        corridorRailingBlock = new BlockBase("corridor_railing_block").setHardness(8.5f).setResistance(45.0f);
        corridorSmooth = new BlockBase("corridor_smooth").setHardness(8.5f).setResistance(45.0f);
        corridorTracks = new BlockColumn("corridor_tracks").setHardness(8.5f).setResistance(45.0f);

        //Void being stuff
        squirmingOrganism = new BlockDamaging("squirming_organism", Material.CORAL, 2, DamageSource.WITHER).setHardness(1.2f).setResistance(2.4f);
        voidBeingRock = new BlockBase("melted_void_being_rock", Material.ROCK, null, SoundType.STONE).setHardness(40.0f).setResistance(500.0f);

        //Ores
        noiseGemOre = new BlockCustomDrop("noise_gem_ore", Material.ROCK, CreativeTabs.BUILDING_BLOCKS, ItemInit.noiseGem, 1, 1);
        rainbowOre = new BlockCustomDrop("rainbow_ore", ItemInit.rainbowGem, 1, 1);

        //Plants
        frozenLeaves = new BlockModLeaves("frozen_leaves");

        //Trustred
        dataDaisy = new BlockDataDaisy();
        dataBlock = new BlockLamp("data_block", Material.CLOTH, 12).setHardness(0.8f).setResistance(2.0f);
        starBlock = new BlockLamp("star_block", Material.CLOTH, 15).setHardness(1.2f).setResistance(3.0f);

        //Mob Drops
        creeperHeart = new BlockCreeperHeart();

        //Decorations
        cardReader = new BlockBase("card_reader").setHardness(5.5f).setResistance(8.0f);
        voidGauge = new BlockSided("void_gauge").setHardness(5.5f).setResistance(8.0f);
        smallMonitor = new BlockSided("small_monitor").setHardness(5.5f).setResistance(8.0f);
        voidGaugeAll = new BlockBase("void_gauge_all").setHardness(5.5f).setResistance(8.0f);
        smallMonitorAll = new BlockBase("small_monitor_all").setHardness(5.5f).setResistance(8.0f);
        voidFan = new BlockBase("void_fan").setHardness(5.5f).setResistance(8.0f);

        //Adminspace
        fan = new BlockBase("fan");
        adminColumn = new BlockColumn("admin_column");
        adminTech = new BlockBase("admin_tech");
        adminspaceGlass = new BlockTranslucent("adminspace_glass");
        adminspaceTile = new BlockBase("adminspace_tile");
        adminspaceWall = new BlockBase("adminspace_wall");
        adminspaceCatwalk = new BlockBase("adminspace_catwalk");
        keySlotter = new BlockKeySlotter();
        mojangBlock = new BlockLamp("mojang_block", 5);
        serverContainer = new BlockServerContainer();
        monitor = new BlockBase("monitor"); //TODO functionality

        //Minesweeper
        minesweeper0 = new BlockMinesweeperTile("minesweeper_0").setHardness(5.5f).setResistance(8.0f);
        minesweeper1 = new BlockMinesweeperTile("minesweeper_1").setHardness(5.5f).setResistance(8.0f);
        minesweeper2 = new BlockMinesweeperTile("minesweeper_2").setHardness(5.5f).setResistance(8.0f);
        minesweeper3 = new BlockMinesweeperTile("minesweeper_3").setHardness(5.5f).setResistance(8.0f);
        minesweeper4 = new BlockMinesweeperTile("minesweeper_4").setHardness(5.5f).setResistance(8.0f);
        minesweeper5 = new BlockMinesweeperTile("minesweeper_5").setHardness(5.5f).setResistance(8.0f);
        minesweeper6 = new BlockMinesweeperTile("minesweeper_6").setHardness(5.5f).setResistance(8.0f);
        minesweeper7 = new BlockMinesweeperTile("minesweeper_7").setHardness(5.5f).setResistance(8.0f);
        minesweeper8 = new BlockMinesweeperTile("minesweeper_8").setHardness(5.5f).setResistance(8.0f);
        minesweeperFlag = new BlockMinesweeperTile("minesweeper_flag").setHardness(5.5f).setResistance(8.0f);
        minesweeperMine = new BlockMinesweeperTile("minesweeper_mine").setHardness(5.5f).setResistance(8.0f);
        minesweeperMineCritical = new BlockMinesweeperTile("minesweeper_mine_critical").setHardness(5.5f).setResistance(8.0f);
        minesweeperButton = new BlockMinesweeperButton().setHardness(5.5f).setResistance(8.0f);

        //Terminals
        terminal = new BlockTerminal("terminal", BlockTerminal.PERM_LEVEL_NORMAL).setHardness(6.0f).setResistance(11.0f);
        mainTerminal = new BlockTerminal("main_terminal", BlockTerminal.PERM_LEVEL_MAIN).setHardness(6.0f).setResistance(11.0f);
        adminspaceTerminal = new BlockBase("adminspace_terminal"); //TODO functionality
        terminalAccept = new BlockTerminalAccept().setHardness(5.5f).setResistance(8.0f);
        terminalDeny = new BlockTerminalDeny().setHardness(5.5f).setResistance(8.0f);

        //Other stuff
        trigger = new BlockTrigger("trigger", Sensitivity.MOBS);

        //Debug
        magicalTeleporterDeltaQuest = new BlockTeleporter("teleporter_dq", 100, 100);
        magicalTeleporterSkySector = new BlockTeleporter("teleporter_ss", 20, 9);
        magicalTeleporterBeyond = new BlockTeleporter("teleporter_by", 23, 100);
        testBlockVisibility = new TestBlocks.Visibility();
        testBlockGenBlock = new TestBlocks.GenBlockTest();
        compactingStructureBlock = new BlockCompactingStructure();
    }
}
