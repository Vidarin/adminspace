package com.vidarin.adminspace.registers;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityRegister {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityVoidChest.class, new ResourceLocation(Adminspace.MODID + ":void_chest"));
        GameRegistry.registerTileEntity(TileEntityTerminal.class, new ResourceLocation(Adminspace.MODID + ":terminal"));
    }
}
