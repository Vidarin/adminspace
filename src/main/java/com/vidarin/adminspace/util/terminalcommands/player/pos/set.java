package com.vidarin.adminspace.util.terminalcommands.player.pos;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermError;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;

public class set {
    public set(TerminalCommandHandler commandHandler, String commandArgs) {
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

            player.setLocationAndAngles(x, y, z, player.cameraYaw, player.cameraPitch);
            if (commandHandler.getWorld().isRemote) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                playerMP.connection.sendPacket(new CPacketPlayer.PositionRotation(x, y, z, playerMP.cameraYaw, player.cameraPitch, playerMP.onGround));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            TermError.argumentError(commandHandler);
        }
    }
}
