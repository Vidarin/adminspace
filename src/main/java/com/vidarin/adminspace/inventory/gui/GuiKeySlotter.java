package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.block.tileentity.TileEntityKeySlotter;
import com.vidarin.adminspace.inventory.container.ContainerDummy;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiKeySlotter extends GuiContainer {
    private static final ResourceLocation TEXTURE_UNLOCKED = new ResourceLocation(Adminspace.MODID, "textures/gui/key_slotter_unlocked.png");
    private static final ResourceLocation TEXTURE_LOCKED = new ResourceLocation(Adminspace.MODID, "textures/gui/key_slotter_locked.png");
    private final TileEntityKeySlotter keySlotter;

    public GuiKeySlotter(TileEntityKeySlotter keySlotter) {
        super(new ContainerDummy(true));
        this.keySlotter = keySlotter;
        this.xSize = 256;
        this.ySize = 256;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(keySlotter.hasKey() ? TEXTURE_UNLOCKED : TEXTURE_LOCKED);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
