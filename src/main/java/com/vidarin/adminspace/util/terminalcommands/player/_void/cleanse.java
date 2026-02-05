package com.vidarin.adminspace.util.terminalcommands.player._void;

import com.vidarin.adminspace.data.PlayerDataHelper;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.entity.player.EntityPlayerMP;

@SuppressWarnings("unused")
public class cleanse {
    public static void execute(TerminalCommandHandler commandHandler, String[] args) {
        if (!commandHandler.getWorld().isRemote && commandHandler.getPlayer() instanceof EntityPlayerMP player) {
            PlayerDataHelper.cleansePlayer(player);
        }
    }
}
