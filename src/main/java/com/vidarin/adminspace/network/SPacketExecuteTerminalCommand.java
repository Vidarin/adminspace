package com.vidarin.adminspace.network;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketExecuteTerminalCommand implements IMessage {
    private String command;
    private BlockPos terminalPos;

    public SPacketExecuteTerminalCommand() {}

    public SPacketExecuteTerminalCommand(String command, BlockPos terminalPos) {
        this.command = command;
        this.terminalPos = terminalPos;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.command);
        buf.writeLong(this.terminalPos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.command = ByteBufUtils.readUTF8String(buf);
        this.terminalPos = BlockPos.fromLong(buf.readLong());
    }

    public static class Handler implements IMessageHandler<SPacketExecuteTerminalCommand, IMessage> {
        @Override
        public IMessage onMessage(SPacketExecuteTerminalCommand message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                TileEntityTerminal terminal = (TileEntityTerminal) player.world.getTileEntity(message.terminalPos);
                if (terminal != null) {
                    TerminalCommandHandler commandHandler = terminal.getCommandHandler();
                    commandHandler.sendCommandParams(player, player.world, terminal);
                    try {
                        commandHandler.runCommand(message.command);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return null;
        }
    }
}
