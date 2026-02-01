package com.vidarin.adminspace.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketSinglePlayerSoundEffect implements IMessage {
    private SoundEvent sound;
    private float volume;
    private float pitch;

    public CPacketSinglePlayerSoundEffect() {}

    public CPacketSinglePlayerSoundEffect(SoundEvent event, float volume, float pitch) {
        this.sound = event;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.sound = SoundEvent.REGISTRY.getObjectById(packetBuffer.readVarInt());
        this.volume = packetBuffer.readFloat();
        this.pitch = packetBuffer.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeVarInt(SoundEvent.REGISTRY.getIDForObject(this.sound));
        packetBuffer.writeFloat(this.volume);
        packetBuffer.writeFloat(this.pitch);
    }

    public static class Handler implements IMessageHandler<CPacketSinglePlayerSoundEffect, IMessage> {
        @Override
        public IMessage onMessage(CPacketSinglePlayerSoundEffect message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(message.sound, message.pitch, message.volume)));
            return null;
        }
    }
}
