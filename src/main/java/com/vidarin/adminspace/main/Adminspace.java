package com.vidarin.adminspace.main;

import com.vidarin.adminspace.data.AdminspaceGlobalData;
import com.vidarin.adminspace.data.AdminspacePlayerData;
import com.vidarin.adminspace.inventory.GuiHandler;
import com.vidarin.adminspace.init.*;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.proxy.CommonProxy;
import com.vidarin.adminspace.util.BundledResourcePack;
import com.vidarin.adminspace.worldgen.WorldGenDataDaisy;
import com.vidarin.adminspace.worldgen.WorldGenOres;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Adminspace.MODID, name = Adminspace.NAME, version = Adminspace.VERSION)
public class Adminspace {
    public static final String MODID = "adminspace";
    public static final String NAME = "Adminspace";
    public static final String VERSION = "0.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance
    public static Adminspace INSTANCE;

    @SidedProxy(clientSide = "com.vidarin.adminspace.proxy.ClientProxy", serverSide = "com.vidarin.adminspace.proxy.CommonProxy")
    public static CommonProxy proxy;

    static {
        SoundInit.registerSounds();
    }

    public static void openGui(EntityPlayer player, World world, BlockPos pos) {
        player.openGui(INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void registerWorldGen() {
        GameRegistry.registerWorldGenerator(new WorldGenOres(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenDataDaisy(), 0);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Adminspace.proxy.preInit(event);
        BundledResourcePack.init();
        AdminspaceGlobalData.init();
        CapabilityManager.INSTANCE.register(AdminspacePlayerData.IData.class, new AdminspacePlayerData.Storage(), AdminspacePlayerData.Data::new);
        EntityInit.registerEntities();
        RegisterRenderers.registerEntityRenderers();
        BiomeInit.registerBiomes();
        DimensionInit.registerDimensions();
        registerWorldGen();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Adminspace.proxy.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
        AdminspaceNetworkHandler.registerPackets();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Adminspace.proxy.postInit(event);
    }
}
