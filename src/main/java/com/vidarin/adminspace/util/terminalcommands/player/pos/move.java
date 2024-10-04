package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class move {
    public move(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();

        try {
            double x;
            try {
                x = Integer.parseInt(commandArgs.split("/")[0]);
            } catch (NumberFormatException e) {
                x = player.posX;
                player.sendMessage(new TextComponentString("<INVALID ARGUMENTS>"));
            }
            double y;
            try {
                y = Integer.parseInt(commandArgs.split("/")[1]);
            } catch (NumberFormatException e) {
                y = player.posY;
                player.sendMessage(new TextComponentString("<INVALID ARGUMENTS>"));
            }
            double z;
            try {
                z = Integer.parseInt(commandArgs.split("/")[2]);
            } catch (NumberFormatException e) {
                z = player.posZ;
                player.sendMessage(new TextComponentString("<INVALID ARGUMENTS>"));
            }

            player.move(MoverType.SELF, x, y, z);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            player.sendMessage(new TextComponentString("<MISSING ARGUMENTS>"));
        }
    }
}
