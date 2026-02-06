package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.inventory.container.ContainerVoidChest;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiVoidChest extends GuiContainer {
    private static final ResourceLocation VOID_CHEST_GUI_TEXTURE = new ResourceLocation(Adminspace.MODID, "textures/gui/void_chest.png");
    private final InventoryPlayer playerInv;
    private final TileEntityVoidChest chest;

    public GuiVoidChest(InventoryPlayer playerInv, TileEntityVoidChest chest, EntityPlayer player) {
        super(new ContainerVoidChest(playerInv, chest, player));
        this.playerInv = playerInv;
        this.chest = chest;

        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.chest.getDisplayName().getUnformattedText(), 8 ,6, 4210752);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 94, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(VOID_CHEST_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
