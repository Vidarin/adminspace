package com.vidarin.adminspace.gui.guis;

import com.vidarin.adminspace.block.BlockTerminal;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiTerminal extends GuiScreen {
    private GuiTextField input;

    private GuiButton executeBtn;

    private final BlockTerminal terminal;

    private FontRenderer fontRendererObj;

    public GuiTerminal(IBlockState state, EntityPlayer player) {
        this.terminal = (BlockTerminal) state.getBlock();
    }

    @Override
    public void initGui() {
        final TerminalCommandHandler commandHandler = terminal.getCommandHandler();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.executeBtn = new GuiButton(0, this.width / 2, this.height / 4, "Execute");
        this.input = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
        this.input.setMaxStringLength(32767);
        this.input.setFocused(true);
        this.executeBtn.enabled = false;
    }

    public void updateGui() {
        TerminalCommandHandler commandHandler = terminal.getCommandHandler();
        this.input.setText("");
        this.executeBtn.enabled = true;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            TerminalCommandHandler commandHandler = terminal.getCommandHandler();
            if (button.id == 0) {
                commandHandler.runCommand(input.getText());

                this.mc.displayGuiScreen(null);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.input.textboxKeyTyped(typedChar, keyCode);

        if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 1)
            {
                this.mc.displayGuiScreen(null);
            }
        }
        else
        {
            this.actionPerformed(this.executeBtn);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.input.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Input RAW Command", this.width / 2, 20, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
