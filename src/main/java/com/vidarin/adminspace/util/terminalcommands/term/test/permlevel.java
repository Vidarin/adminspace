package com.vidarin.adminspace.util.terminalcommands.term.test;

import com.vidarin.adminspace.util.Fonts;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.util.Objects;

@SuppressWarnings("unused")
public class permlevel {
    public static void execute(TerminalCommandHandler commandHandler, String[] args) {
        int permLevel = commandHandler.getPermLevel();
        EntityPlayer player = commandHandler.getPlayer();

        if (!commandHandler.getWorld().isRemote) {
            if (Objects.equals(permLevel, 0)) player.sendMessage(new TextComponentString(TermUtil.getFancyErrorMessage(commandHandler.getWorld().rand, "EMPTY_MEMORY")));
            else if (Objects.equals(permLevel, 1)) player.sendMessage(new TextComponentString(Fonts.DarkGreen + "< NORMAL >"));
            else if (Objects.equals(permLevel, 2)) player.sendMessage(new TextComponentString(Fonts.Cyan + "< MAIN >"));
            else if (Objects.equals(permLevel, 3)) player.sendMessage(new TextComponentString(Fonts.DarkRed + "< ADMINSPACE >"));
            else if (Objects.equals(permLevel, 4)) player.sendMessage(new TextComponentString(Fonts.Purple + "< AUTHOR >"));
        }
    }
}
