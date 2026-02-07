package com.vidarin.adminspace.network;

import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.inventory.gui.GuiInstruction;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketOpenInstruction implements IMessage {
    private EnumHand hand;

    public CPacketOpenInstruction() {}

    public CPacketOpenInstruction(EnumHand hand) {
        this.hand = hand;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(hand);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.hand = buffer.readEnumValue(EnumHand.class);
    }

    public static class Handler implements IMessageHandler<CPacketOpenInstruction, IMessage> {
        @Override
        public IMessage onMessage(CPacketOpenInstruction message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                ItemStack stack = message.hand == EnumHand.OFF_HAND ? mc.player.getHeldItemOffhand() : mc.player.getHeldItemMainhand();

                if (stack.getItem() == ItemInit.instruction) {
                    mc.displayGuiScreen(new GuiInstruction(mc.player, stack));
                }
            });
            return null;
        }
    }
}
