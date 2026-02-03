package com.vidarin.adminspace.util.terminalcommands;

import com.vidarin.adminspace.util.Fonts;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class TermUtil {
    /** Reflectively called if you type a nonexistent class in {@link TerminalCommandHandler} */
    @SuppressWarnings("unused")
    public static void execute(TerminalCommandHandler commandHandler, String[] args) {
        syntaxError(commandHandler);
    }

    public static void argumentError(TerminalCommandHandler commandHandler) {
        if (commandHandler.getWorld().isRemote) {
            EntityPlayer player = commandHandler.getPlayer();

            player.sendMessage(new TextComponentString(getFancyErrorMessage(commandHandler.getWorld().rand, "MALFORMED_ARGS")));
        }
    }

    public static void syntaxError(TerminalCommandHandler commandHandler) {
        if (commandHandler.getWorld().isRemote) {
            EntityPlayer player = commandHandler.getPlayer();

            player.sendMessage(new TextComponentString(getFancyErrorMessage(commandHandler.getWorld().rand, "INVALID_SYNTAX")));
        }
    }

    public static boolean parseBoolean(String s) {
        return Arrays.asList("true", "yes", "y", "1").contains(s.toLowerCase(Locale.ROOT));
    }

    public static boolean checkPerms(TerminalCommandHandler commandHandler, int required) {
        if (commandHandler.getPermLevel() < required) {
            if (commandHandler.getWorld().isRemote) commandHandler.getPlayer().sendMessage(new TextComponentString(getFancyErrorMessage(commandHandler.getWorld().rand, "PERMISSION_DENIED")));
            return false;
        }
        return true;
    }

    public static String getFancyErrorMessage(Random rand, String type) {
        return String.format(Fonts.Red + "< FATAL ERROR OF TYPE %s AT ADDRESS %X >", type, rand.nextInt(0xEFFF) + 0x1000);
    }
}
