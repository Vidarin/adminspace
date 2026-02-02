package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.data.AdminspaceGlobalData;
import com.vidarin.adminspace.main.Adminspace;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

@SideOnly(Side.CLIENT)
public class GuiResourcePackNotice extends GuiScreen {
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
        drawCenteredLink("© lassebq - Licensed under CC BY-NC 3.0", "https://creativecommons.org/licenses/by-nc/3.0/", this.width / 2, this.height / 4 + 54 , mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private final List<Pair<BiPredicate<Integer, Integer>, String>> clickableLinks = new ArrayList<>();
    private final Int2ObjectMap<String> buttonIdToUrl = new Int2ObjectArrayMap<>();
    private int i = 100;

    private void drawCenteredLink(String text, String url, int x, int y, int mouseX, int mouseY) {
        int linkX = x - fontRenderer.getStringWidth(text) / 2;
        int linkWidth = fontRenderer.getStringWidth(text);
        int linkHeight = fontRenderer.FONT_HEIGHT;

        BiPredicate<Integer, Integer> hovered = (mx, my) -> mx >= linkX && mx <= linkX + linkWidth &&
                                                                           my >= y && my <= y + linkHeight;

        clickableLinks.add(Pair.of(hovered, url));

        int color = hovered.test(mouseX, mouseY) ? 0x55AAFF : 0x3366FF;
        fontRenderer.drawString(text, linkX, y, color, false);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Pair<BiPredicate<Integer, Integer>, String> pair : clickableLinks) {
            if (pair.getLeft().test(mouseX, mouseY)) {
                i++;
                buttonIdToUrl.put(i, pair.getRight());
                mc.displayGuiScreen(new GuiConfirmOpenLink(this, pair.getRight(), i, true));
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        String url = buttonIdToUrl.remove(id);
        if (url != null && result) {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (IOException e) {
                Adminspace.LOGGER.error("Could not open link {}", url, e);
            }
        }
        mc.displayGuiScreen(this);
    }
}
