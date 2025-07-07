package com.vidarin.adminspace.util.terminalcommands;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class TermError {
    public TermError(TerminalCommandHandler commandHandler, String commandArgs) {
        if (commandHandler.getWorld().isRemote) {
            EntityPlayer player = commandHandler.getPlayer();

            player.sendMessage(new TextComponentString("<SYNTAX ERROR>"));
        }
    }

    public static void argumentError(TerminalCommandHandler commandHandler) {
        if (commandHandler.getWorld().isRemote) {
            EntityPlayer player = commandHandler.getPlayer();

            player.sendMessage(new TextComponentString("<INVALID ARGUMENTS>"));
        }
    }
}
