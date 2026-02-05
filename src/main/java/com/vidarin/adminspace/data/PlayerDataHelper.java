package com.vidarin.adminspace.data;

import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.CPacketUpdatePlayerData;
import net.minecraft.entity.player.EntityPlayerMP;

public final class PlayerDataHelper {
    public static void sendBlindnessUpdate(EntityPlayerMP player, int value) {
        Adminspace.LOGGER.debug("Updated player {}'s blinded duration to {}", player.getName(), value);
        AdminspacePlayerData.getData(player).setBlindedDuration(value);
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketUpdatePlayerData(CPacketUpdatePlayerData.Type.BlindedDuration, value), player);
    }

    public static void setPlayerVisitedBeyond(EntityPlayerMP player) {
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketUpdatePlayerData(CPacketUpdatePlayerData.Type.VisitedBeyond, true), player);
    }

    public static void cleansePlayer(EntityPlayerMP player) {
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketUpdatePlayerData(CPacketUpdatePlayerData.Type.VisitedBeyond, false), player);
    }
}
