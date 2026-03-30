package com.vidarin.adminspace.init;

import com.vidarin.adminspace.block.TestBlocks;
import com.vidarin.adminspace.block.special.BlockCompactingStructure;
import com.vidarin.adminspace.block.tileentity.*;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityInit {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityVoidChest.class, new ResourceLocation(Adminspace.MODID, "void_chest"));
        GameRegistry.registerTileEntity(TileEntityTerminal.class, new ResourceLocation(Adminspace.MODID, "terminal"));
        GameRegistry.registerTileEntity(TileEntityMinesweeperLogic.class, new ResourceLocation(Adminspace.MODID, "minesweeper_logic"));
        GameRegistry.registerTileEntity(TileEntityKeySlotter.class, new ResourceLocation(Adminspace.MODID, "key_slotter"));
        GameRegistry.registerTileEntity(TileEntityServerContainer.class, new ResourceLocation(Adminspace.MODID, "server_container"));

        GameRegistry.registerTileEntity(TestBlocks.Visibility.TileEntityVisibilityTest.class, new ResourceLocation(Adminspace.MODID, "visibility_test"));
        GameRegistry.registerTileEntity(TestBlocks.GenBlockTest.TileEntityGenBlockTest.class, new ResourceLocation(Adminspace.MODID, "gen_block_test"));
        GameRegistry.registerTileEntity(BlockCompactingStructure.TileEntityCompactingStructureBlock.class, new ResourceLocation(Adminspace.MODID, "compacting_structure_block"));
    }
}
