package com.vidarin.adminspace.util.terminalcommands.term.test;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.util.Objects;

public class permlevel {
    public permlevel(TerminalCommandHandler commandHandler, String commandArgs) {
        int permLevel = commandHandler.getPermLevel();
        EntityPlayer player = commandHandler.getPlayer();

        if (Objects.equals(permLevel, 0)) player.sendMessage(new TextComponentString("<MEMORY SLOT EMPTY: NO DATA FOUND>"));
        else if (Objects.equals(permLevel, 1)) player.sendMessage(new TextComponentString("<Normal>"));
        else if (Objects.equals(permLevel, 2)) player.sendMessage(new TextComponentString("<Main>"));
        else if (Objects.equals(permLevel, 3)) player.sendMessage(new TextComponentString("<Adminspace>"));
        else if (Objects.equals(permLevel, 4)) player.sendMessage(new TextComponentString("<Author>"));
    }
}
