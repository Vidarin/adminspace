package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class move {
    public static void execute(TerminalCommandHandler commandHandler, String[] args) {
        EntityPlayer player = commandHandler.getPlayer();

        if (TermUtil.checkPerms(commandHandler, 1)) {
            if (args.length < 3) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            double x;
            try {
                x = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            double y;
            try {
                y = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            double z;
            try {
                z = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            boolean respectWalls = false;
            if (args.length == 4) respectWalls = TermUtil.parseBoolean(args[3]);

            if (respectWalls) player.move(MoverType.SELF, x, y, z);
            else player.setPositionAndUpdate(player.posX + x, player.posY + y, player.posZ + z);

        }
    }
}
