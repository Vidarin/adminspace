package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.main.Adminspace;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class GuiScreenWithLinks extends GuiScreen {
    private final List<Pair<BiPredicate<Integer, Integer>, String>> clickableLinks = new ArrayList<>();
    private final Int2ObjectMap<String> buttonIdToUrl = new Int2ObjectArrayMap<>();
    private int i = 100;

    protected void drawCenteredLink(String text, String url, int x, int y, int mouseX, int mouseY) {
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
