package com.vidarin.adminspace.main;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.data.AdminspaceGlobalData;
import com.vidarin.adminspace.data.AdminspacePlayerData;
import com.vidarin.adminspace.dimension.beyond.DimensionBeyond;
import com.vidarin.adminspace.inventory.gui.GuiResourcePackNotice;
import com.vidarin.adminspace.model.render.RenderVoidChest;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.init.TileEntityInit;
import com.vidarin.adminspace.util.SpookyTextRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Adminspace.MODID)
public class AdminspaceEventHandler {

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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Adminspace.MODID, "player_data"), new AdminspacePlayerData.Provider());
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer player) {
            AdminspacePlayerData.IData data = AdminspacePlayerData.getData(player);
            if (data == null) return;

            if (data.getBlindedDuration() > 0) data.setBlindedDuration(data.getBlindedDuration() - 1);
        }
    }

    @SubscribeEvent
    public static void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            if (event.toDim == 23) DimensionBeyond.onPlayerChangedDimension(player);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderTextOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null || mc.gameSettings.showDebugInfo) return;

        AdminspacePlayerData.IData data = AdminspacePlayerData.getData(player);
        if (data == null) return;

        if (AdminspaceGlobalData.hasVisitedBeyond()) SpookyTextRenderer.doTheThing("Please remove this version from your system.", mc.fontRenderer, 5, 5);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    @SideOnly(Side.CLIENT)
    public static void onGuiOpen(GuiOpenEvent event) {
        if (!AdminspaceGlobalData.hasShownResourcePackNotice() && event.getGui() instanceof GuiMainMenu) {
            event.setGui(new GuiResourcePackNotice(event.getGui()));
        }
    }
}
