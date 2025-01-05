package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermError;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;

public class move {
    public move(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();

        try {
            double x;
            try {
                x = Integer.parseInt(commandArgs.split("/")[0]);
            } catch (NumberFormatException e) {
                x = player.posX;
                TermError.argumentError(commandHandler);
            }
            double y;
            try {
                y = Integer.parseInt(commandArgs.split("/")[1]);
            } catch (NumberFormatException e) {
                y = player.posY;
                TermError.argumentError(commandHandler);
            }
            double z;
            try {
                z = Integer.parseInt(commandArgs.split("/")[2]);
            } catch (NumberFormatException e) {
                z = player.posZ;
                TermError.argumentError(commandHandler);
            }

            player.move(MoverType.SELF, x, y, z);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            TermError.argumentError(commandHandler);
        }
    }
}
