package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.data.AdminspaceGlobalData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GuiResourcePackNotice extends GuiScreenWithLinks {
    private final GuiScreen prev;

    public GuiResourcePackNotice(GuiScreen prev) {
        this.prev = prev;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 50, "Got it"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 30, "Resource Packs"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            AdminspaceGlobalData.setShownResourcePackNotice(true);
            this.mc.displayGuiScreen(prev);
        } else if (button.id == 1) {
            AdminspaceGlobalData.setShownResourcePackNotice(true);
            this.mc.displayGuiScreen(new GuiScreenResourcePacks(prev));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        drawCenteredString(this.fontRenderer, "Optional resource pack available", this.width / 2, this.height / 4 - 40, 0xFFFFFF);
        drawCenteredString(this.fontRenderer, "Adminspace comes bundled with \"AlphaPack - The Old MC look\" by lassebq", this.width / 2, this.height / 4 - 20 , 0xCCCCCC);
        drawCenteredString(this.fontRenderer, "The pack is not required, but provides", this.width / 2, this.height / 4 - 8 , 0xCCCCCC);
        drawCenteredString(this.fontRenderer, "a more accurate experience to the actual TBoTV ARG", this.width / 2, this.height / 4 + 4 , 0xCCCCCC);
        drawCenteredString(this.fontRenderer, "You can enable the pack in Options -> Resource Packs", this.width / 2, this.height / 4 + 16 , 0xCCCCCC);
        drawCenteredString(this.fontRenderer, "(AlphaPack has some features that require OptiFine, but it works fine without)", this.width / 2, this.height / 4 + 28 , 0xCCCCCC);

        drawCenteredLink("AlphaPack on CurseForge", "https://www.curseforge.com/minecraft/texture-packs/alpha-pack-lassebq", this.width / 2, this.height / 4 + 42 , mouseX, mouseY);
        drawCenteredLink("AlphaPack © lassebq - Licensed under CC BY-NC 3.0", "https://creativecommons.org/licenses/by-nc/3.0/", this.width / 2, this.height / 4 + 54 , mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
