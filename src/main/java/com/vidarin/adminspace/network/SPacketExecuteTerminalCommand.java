package com.vidarin.adminspace.network;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
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
        buf.writeInt(this.terminalPos.getX());
        buf.writeInt(this.terminalPos.getY());
        buf.writeInt(this.terminalPos.getZ());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.command = ByteBufUtils.readUTF8String(buf);
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        terminalPos = new BlockPos(x, y, z);
    }

    public static class Handler implements IMessageHandler<SPacketExecuteTerminalCommand, IMessage> {
        @Override
        public IMessage onMessage(SPacketExecuteTerminalCommand message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                TileEntity te =  player.world.getTileEntity(message.terminalPos);
                if (te instanceof TileEntityTerminal terminal) {
                    TerminalCommandHandler commandHandler = terminal.getCommandHandler();
                    commandHandler.sendCommandParams(player, player.world, terminal);
                    commandHandler.runCommand(message.command);
                }
            });
            return null;
        }
    }
}
