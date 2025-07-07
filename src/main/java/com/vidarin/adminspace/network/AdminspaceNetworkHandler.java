package com.vidarin.adminspace.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class AdminspaceNetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("VOID|Main");

    public static void registerPackets() {
        INSTANCE.registerMessage(SPacketCompleteInstruction.Handler.class, SPacketCompleteInstruction.class, 0, Side.SERVER);
        INSTANCE.registerMessage(SPacketExecuteTerminalCommand.Handler.class, SPacketExecuteTerminalCommand.class, 1, Side.SERVER);
        INSTANCE.registerMessage(SPacketUpdateVariablesMap.Handler.class, SPacketUpdateVariablesMap.class, 2, Side.SERVER);
    }
}
