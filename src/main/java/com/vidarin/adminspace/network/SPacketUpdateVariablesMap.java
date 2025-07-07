package com.vidarin.adminspace.network;

import com.vidarin.adminspace.data.AdminspaceVariables;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SPacketUpdateVariablesMap implements IMessage {
    private String map;
    private String key;
    private Object value;

    public SPacketUpdateVariablesMap() {}

    public SPacketUpdateVariablesMap(String map, String key, Object value) {
        this.map = map;
        this.key = key;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.map = ByteBufUtils.readUTF8String(buf);
        this.key = ByteBufUtils.readUTF8String(buf);
        switch (buf.readByte()) {
            case 0:
                this.value = ByteBufUtils.readUTF8String(buf);
                break;
            case 1:
                this.value = buf.readInt();
                break;
            case 2:
                this.value = buf.readFloat();
                break;
            case 3:
                this.value = buf.readBoolean();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.map);
        ByteBufUtils.writeUTF8String(buf, this.key);
        if (value instanceof String) {
            buf.writeByte(0);
            ByteBufUtils.writeUTF8String(buf, (String) this.value);
        }
        if (value instanceof Integer) {
            buf.writeByte(1);
            buf.writeInt((int) this.value);
        }
        if (value instanceof Float) {
            buf.writeByte(2);
            buf.writeFloat((float) this.value);
        }
        if (value instanceof Boolean) {
            buf.writeByte(3);
            buf.writeBoolean((boolean) this.value);
        }
    }

    public static class Handler implements IMessageHandler<SPacketUpdateVariablesMap, IMessage> {
        @Override
        public IMessage onMessage(SPacketUpdateVariablesMap message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                switch (message.map) {
                    case "ambientOcclusion":
                        int value = (int) message.value;
                        AdminspaceVariables.get(player.world).setAmbientOcclusionValue(UUID.fromString(message.key), value);
                        break;
                }
            });
            return null;
        }
    }
}
