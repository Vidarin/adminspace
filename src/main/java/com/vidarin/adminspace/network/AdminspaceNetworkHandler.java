package com.vidarin.adminspace.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class AdminspaceNetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("Adminspace|Main");

    public static void registerPackets() { // IMPORTANT: SPacket here means TO server, not FROM server like in vanilla minecraft. Any complaints can be submitted to my business email kakkabartfjart6@gmail.com
        INSTANCE.registerMessage(SPacketCompleteInstruction.Handler.class, SPacketCompleteInstruction.class, 0, Side.SERVER);
        INSTANCE.registerMessage(SPacketExecuteTerminalCommand.Handler.class, SPacketExecuteTerminalCommand.class, 1, Side.SERVER);
        INSTANCE.registerMessage(SPacketUpdateVariablesMap.Handler.class, SPacketUpdateVariablesMap.class, 2, Side.SERVER);

        INSTANCE.registerMessage(CPacketUpdatePlayerData.Handler.class, CPacketUpdatePlayerData.class, 100, Side.CLIENT);
        INSTANCE.registerMessage(CPacketSinglePlayerSoundEffect.Handler.class, CPacketSinglePlayerSoundEffect.class, 101, Side.CLIENT);
    }
}
