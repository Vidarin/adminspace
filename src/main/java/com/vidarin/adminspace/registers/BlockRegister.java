package com.vidarin.adminspace.registers;

import com.vidarin.adminspace.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockRegister {
    public static final List<Block> BLOCKS;

    public static final Block voidTile;
    public static final Block voidErrTile;

    public static final Block voidLamp;

    public static final Block voidDoor;

    public static final Block voidChest;

    public static final Block voidGlass;

    public static final Block voidCreep;
    public static final Block voidCorruption;

    public static final Block terminal;

    public static final Block trigger;

    static {
        BLOCKS = new ArrayList<>();

        voidTile = new BlockBase("void_tile");
        voidErrTile = new BlockBase("void_err_tile");

        voidLamp = new BlockLamp("void_lamp");

        voidDoor = new BlockModDoor("void_door", Material.IRON);

        voidChest = new BlockVoidChest("void_chest");

        voidGlass = new BlockTransparent("void_glass");

        voidCreep = new BlockCreep("void_creep"); //Or as RGN would call it: "blueish substance"
        voidCorruption = new BlockCreep("void_corruption");

        terminal = new BlockTerminal();

        trigger = new BlockTrigger("trigger", BlockPressurePlate.Sensitivity.MOBS);
    }
}
