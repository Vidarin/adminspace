package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.entity.player.EntityPlayer;

public class set {
    public set(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();

        if (TermUtil.checkPerms(commandHandler, 1)) {
            try {
                double x;
                try {
                    x = Integer.parseInt(commandArgs.split("/")[0]);
                } catch (NumberFormatException e) {
                    x = player.posX;
                    TermUtil.argumentError(commandHandler);
                }
                double y;
                try {
                    y = Integer.parseInt(commandArgs.split("/")[1]);
                } catch (NumberFormatException e) {
                    y = player.posY;
                    TermUtil.argumentError(commandHandler);
                }
                double z;
                try {
                    z = Integer.parseInt(commandArgs.split("/")[2]);
                } catch (NumberFormatException e) {
                    z = player.posZ;
                    TermUtil.argumentError(commandHandler);
                }

                player.setPositionAndUpdate(x, y, z);
            } catch (ArrayIndexOutOfBoundsException e) {
                TermUtil.argumentError(commandHandler);
            }
        }
    }
}
