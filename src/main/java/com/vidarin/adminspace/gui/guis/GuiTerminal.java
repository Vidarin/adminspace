package com.vidarin.adminspace.gui.guis;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.gui.containers.ContainerDummy;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiTerminal extends GuiContainer {
    private GuiTextField input;

    private GuiButton executeBtn;

    private final TileEntityTerminal terminal;

    private final EntityPlayer player;
    private final World world;

    public GuiTerminal(TileEntityTerminal terminal, EntityPlayer player) {
        super(new ContainerDummy(true));
        this.terminal = terminal;
        this.player = player;
        this.world = terminal.getWorld();
    }

    @Override
    public void initGui() {
        TerminalCommandHandler commandHandler = terminal.getCommandHandler();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.executeBtn = new GuiButton(0, this.width / 2 - 100, this.height - 70, "Execute");
        this.input = new GuiTextField(1, this.fontRenderer, this.width / 2 - 150, this.height / 2 - 50, 300, 20);
        this.input.setMaxStringLength(32767);
        this.input.setFocused(true);
        this.input.setText(commandHandler.getCommandStored());
        this.executeBtn.enabled = true;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            TerminalCommandHandler commandHandler = terminal.getCommandHandler();
            if (button.id == 0) {
                commandHandler.sendCommandParams(this.player, this.world, this.terminal);
                try {
                    commandHandler.runCommand(input.getText());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                this.mc.displayGuiScreen(null);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        this.input.textboxKeyTyped(typedChar, keyCode);
        TerminalCommandHandler commandHandler = terminal.getCommandHandler();

        if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 1)
            {
                this.mc.displayGuiScreen(null);
                commandHandler.setCommandStored(input.getText());
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
        if (this.executeBtn.x <= mouseX && this.executeBtn.x + 200 >= mouseX && this.executeBtn.y <= mouseY && this.executeBtn.y + 20 >= mouseY) {
            this.actionPerformed(this.executeBtn);
            this.world.playSound(this.player, this.terminal.getPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.executeBtn.drawButton(this.mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString("Input RAW Command", this.width / 2 - 50 ,this.height / 5, 16777215);
        this.input.drawTextBox();
    }
}
