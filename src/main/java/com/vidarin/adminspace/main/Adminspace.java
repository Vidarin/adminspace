package com.vidarin.adminspace.main;

import com.vidarin.adminspace.gui.GuiHandler;
import com.vidarin.adminspace.init.EntityInit;
import com.vidarin.adminspace.proxy.CommonProxy;
import com.vidarin.adminspace.init.SoundInit;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Adminspace.MODID, name = Adminspace.NAME, version = Adminspace.VERSION)
public class Adminspace
{
    public static final String MODID = "adminspace";
    public static final String NAME = "Adminspace";
    public static final String VERSION = "0.0.0";

    @Mod.Instance
    public static Adminspace INSTANCE;

    @SidedProxy(clientSide = "com.vidarin.adminspace.proxy.ClientProxy", serverSide = "com.vidarin.adminspace.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Adminspace.proxy.preInit(event);
        EntityInit.registerEntities();
        RegisterRenderers.registerEntityRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Adminspace.proxy.init();
        SoundInit.registerSounds();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Adminspace.proxy.postInit(event);
    }
}
