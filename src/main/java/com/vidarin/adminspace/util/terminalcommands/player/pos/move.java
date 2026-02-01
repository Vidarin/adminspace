package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;

public class move {
    public move(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();

        if (TermUtil.checkPerms(commandHandler, 1)) {
            try {
                String[] args = commandArgs.split("/");
                double x;
                try {
                    x = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    x = 0;
                    TermUtil.argumentError(commandHandler);
                }
                double y;
                try {
                    y = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    y = 0;
                    TermUtil.argumentError(commandHandler);
                }
                double z;
                try {
                    z = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    z = 0;
                    TermUtil.argumentError(commandHandler);
                }
                boolean respectWalls = false;
                if (args.length == 4) respectWalls = TermUtil.parseBoolean(args[3]);

                if (respectWalls) player.move(MoverType.SELF, x, y, z);
                else player.setPositionAndUpdate(player.posX + x, player.posY + y, player.posZ + z);
            } catch (ArrayIndexOutOfBoundsException e) {
                TermUtil.argumentError(commandHandler);
            }
        }
    }
}
