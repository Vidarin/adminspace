package com.vidarin.adminspace.main;

import com.vidarin.adminspace.packets.BreakBlockPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("adminspacechannel");

    public static void init() {
        INSTANCE.registerMessage(ServerHandler.class, BreakBlockPacket.class, 0, Side.SERVER);
    }

    public static class ServerHandler implements IMessageHandler<BreakBlockPacket, IMessage> {
        @Override
        public IMessage onMessage(BreakBlockPacket message, MessageContext ctx) {
            BlockPos blockPos = message.getBlockPos();
            ctx.getServerHandler().player.getServerWorld().setBlockToAir(blockPos);
            return null;
        }
    }
}
