package com.vidarin.adminspace.network;

import com.vidarin.adminspace.data.AdminspaceGlobalData;
import com.vidarin.adminspace.data.AdminspacePlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CPacketUpdatePlayerData implements IMessage {
    private Type type;
    private Object value;

    public CPacketUpdatePlayerData() {}

    public CPacketUpdatePlayerData(Type type, Object value) { this.type = type; this.value = value; }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.VALUES.get(ByteBufUtils.readUTF8String(buf));
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
        ByteBufUtils.writeUTF8String(buf, type.toString());
        if (value instanceof String) {
            buf.writeByte(0);
            ByteBufUtils.writeUTF8String(buf, (String) this.value);
        }
        else if (value instanceof Integer) {
            buf.writeByte(1);
            buf.writeInt((int) this.value);
        }
        else if (value instanceof Float) {
            buf.writeByte(2);
            buf.writeFloat((float) this.value);
        }
        else if (value instanceof Boolean) {
            buf.writeByte(3);
            buf.writeBoolean((boolean) this.value);
        }
    }

    public static class Handler implements IMessageHandler<CPacketUpdatePlayerData, IMessage> {
        @Override
        public IMessage onMessage(CPacketUpdatePlayerData message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player == null) return;

                AdminspacePlayerData.IData data = AdminspacePlayerData.getData(player);
                if (data == null) return;

                message.type.update(message.value, player);
            });
            return null;
        }
    }

    public enum Type {
        VisitedBeyond(AdminspaceGlobalData::setVisitedBeyond),
        BlindedDuration(AdminspacePlayerData.IData::setBlindedDuration);

        private static final Map<String, Type> VALUES = new HashMap<>();

        private final Object updater;
        private final boolean global;

        <T> Type(BiConsumer<AdminspacePlayerData.IData, T> updater) {
            this.updater = updater;
            this.global = false;
        }

        <T> Type(Consumer<T> updater) {
            this.updater = updater;
            this.global = true;
        }

        @SuppressWarnings("unchecked")
        public <T> void update(T v, EntityPlayer player) {
            if (!global) ((BiConsumer<AdminspacePlayerData.IData, T>) updater).accept(AdminspacePlayerData.getData(player), v);
            else ((Consumer<T>) updater).accept(v);
        }

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }

        static {
            for (Type t : values()) VALUES.put(t.toString(), t);
        }
    }
}
