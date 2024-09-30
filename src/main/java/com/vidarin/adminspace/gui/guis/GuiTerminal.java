package com.vidarin.adminspace.gui.guis;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTerminal extends GuiScreen {
    private GuiTextField input;

    private GuiButton executeBtn;

    private PropertyBool isMain;
    private Block terminal;

    public GuiTerminal(IBlockState state, EntityPlayer player) {
       isMain = (PropertyBool) state.getBlock().getBlockState().getProperty("IS_MAIN");
       terminal = state.getBlock();
    }

    @Override
    public void initGui() {
        super.initGui();
    }
}
