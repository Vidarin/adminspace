package com.vidarin.adminspace.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {}

    public void init() {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void registerItemRenderer(Item item, int meta, String id) {}
}
