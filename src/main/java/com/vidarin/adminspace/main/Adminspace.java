package com.vidarin.adminspace.main;

import com.vidarin.adminspace.gui.GuiHandler;
import com.vidarin.adminspace.init.*;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Adminspace.MODID, name = Adminspace.NAME, version = Adminspace.VERSION)
public class Adminspace
{
    public static final String MODID = "adminspace";
    public static final String NAME = "Adminspace";
    public static final String VERSION = "0.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance
    public static Adminspace INSTANCE;

    @SidedProxy(clientSide = "com.vidarin.adminspace.proxy.ClientProxy", serverSide = "com.vidarin.adminspace.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Adminspace.proxy.preInit(event);
        EntityInit.registerEntities();
        RegisterRenderers.registerEntityRenderers();
        BiomeInit.registerBiomes();
        DimensionInit.registerDimensions();
        MainRegistry.registerWorldGen();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Adminspace.proxy.init();
        SoundInit.registerSounds();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
        AdminspaceNetworkHandler.registerPackets();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Adminspace.proxy.postInit(event);
    }
}
