package com.vidarin.adminspace.init;

import com.vidarin.adminspace.block.tileentity.TileEntityMinesweeperLogic;
import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityInit {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityVoidChest.class, new ResourceLocation(Adminspace.MODID, "void_chest"));
        GameRegistry.registerTileEntity(TileEntityTerminal.class, new ResourceLocation(Adminspace.MODID, "terminal"));
        GameRegistry.registerTileEntity(TileEntityMinesweeperLogic.class, new ResourceLocation(Adminspace.MODID, "minesweeper_logic"));
    }
}
