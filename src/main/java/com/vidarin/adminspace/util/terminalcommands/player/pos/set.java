package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.block.special.BlockTerminal;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class set {
    public static void execute(TerminalCommandHandler commandHandler, String[] args) {
        EntityPlayer player = commandHandler.getPlayer();

        if (TermUtil.checkPerms(commandHandler, BlockTerminal.PERM_LEVEL_NORMAL)) {
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

            player.setPositionAndUpdate(x, y, z);
        }
    }
}
