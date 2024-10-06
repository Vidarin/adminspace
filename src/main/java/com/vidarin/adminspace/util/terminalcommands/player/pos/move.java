package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermError;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;

public class move {
    public move(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();

        TermError termError = new TermError(commandHandler, commandArgs);

        try {
            double x;
            try {
                x = Integer.parseInt(commandArgs.split("/")[0]);
            } catch (NumberFormatException e) {
                x = player.posX;
                termError.argumentError(commandHandler);
            }
            double y;
            try {
                y = Integer.parseInt(commandArgs.split("/")[1]);
            } catch (NumberFormatException e) {
                y = player.posY;
                termError.argumentError(commandHandler);
            }
            double z;
            try {
                z = Integer.parseInt(commandArgs.split("/")[2]);
            } catch (NumberFormatException e) {
                z = player.posZ;
                termError.argumentError(commandHandler);
            }

            player.move(MoverType.SELF, x, y, z);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            termError.argumentError(commandHandler);
        }
    }
}
