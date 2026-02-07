package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.block.tileentity.TileEntityServerContainer;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiServerContainer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Adminspace.MODID, "textures/gui/server_container.png");
    private final InventoryPlayer playerInv;
    private final TileEntityServerContainer serverContainer;

    public GuiServerContainer(InventoryPlayer playerInv, TileEntityServerContainer serverContainer, Container container) {
        super(container);
        this.playerInv = playerInv;
        this.serverContainer = serverContainer;
        this.xSize = 175;
        this.ySize = 193;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.serverContainer.getDisplayName().getUnformattedText(),  this.xSize / 2 - this.fontRenderer.getStringWidth(this.serverContainer.getDisplayName().getUnformattedText()) / 2 ,6, 0x404040);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 108, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
