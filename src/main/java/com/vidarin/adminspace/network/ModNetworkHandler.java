package com.vidarin.adminspace.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("VOID|Main");

    public static void registerPackets() {
        INSTANCE.registerMessage(CompleteInstructionPacket.Handler.class, CompleteInstructionPacket.class, 0, Side.SERVER);
        INSTANCE.registerMessage(ExecuteTerminalCommandPacket.Handler.class, ExecuteTerminalCommandPacket.class, 1, Side.SERVER);
    }
}
