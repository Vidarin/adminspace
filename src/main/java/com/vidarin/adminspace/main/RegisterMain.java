package com.vidarin.adminspace.main;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.model.render.RenderVoidChest;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.init.TileEntityInit;
import com.vidarin.adminspace.worldgen.WorldGenOres;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class RegisterMain {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
        TileEntityInit.registerTileEntities();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVoidChest.class, new RenderVoidChest());
    }

    public static void registerWorldGen() {
        GameRegistry.registerWorldGenerator(new WorldGenOres(), 0);
    }
}
