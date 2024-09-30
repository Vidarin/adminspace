package com.vidarin.adminspace.main;

import com.vidarin.adminspace.registers.BlockRegister;
import com.vidarin.adminspace.registers.ItemRegister;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class RegisterModels {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        for (final Item item : ItemRegister.ITEMS) {
            Adminspace.proxy.registerItemRenderer(item, 0, "inventory");
        }
        for (final Block block : BlockRegister.BLOCKS) {
            Adminspace.proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");
        }
    }
}
