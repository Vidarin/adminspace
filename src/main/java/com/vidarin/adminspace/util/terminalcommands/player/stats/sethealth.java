package com.vidarin.adminspace.util.terminalcommands.player.stats;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermError;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Objects;

public class sethealth {
    public sethealth(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer target = commandHandler.getPlayer();

        try {
            String targetName = commandArgs.split("/")[0];
            int val = 0;
            try {
                val = Integer.parseInt(commandArgs.split("/")[1]);
            } catch (NumberFormatException ignored) {
            }
            if (!Objects.equals(targetName, "@")) {
                target = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(targetName);
                if (target == null) {
                    target = commandHandler.getPlayer();
                    target.sendMessage(new TextComponentString("<INVALID ARGUMENTS>"));
                }
            }

            assert target != null;

            target.setHealth(val);
            if (commandHandler.getWorld().isRemote) {
                EntityPlayerMP playerMP = (EntityPlayerMP) target;
                playerMP.connection.sendPacket(new SPacketUpdateHealth(val, 0, 0F));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            TermError.argumentError(commandHandler);
        }
    }
}
