package com.vidarin.adminspace.network;

import com.vidarin.adminspace.init.ItemInit;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class SPacketCompleteInstruction implements IMessage {
    private ItemStack stack;

    public SPacketCompleteInstruction() {}

    public SPacketCompleteInstruction(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeItemStack(stack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            this.stack = packetBuffer.readItemStack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Handler implements IMessageHandler<SPacketCompleteInstruction, IMessage> {

        @Override
        public IMessage onMessage(SPacketCompleteInstruction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack stack = message.stack;
                if (stack.getItem() == ItemInit.instruction) {
                    NBTTagCompound tag = stack.getTagCompound();

                    if (tag != null && tag.hasKey("pages", Constants.NBT.TAG_LIST)) {
                        ItemStack newStack = new ItemStack(ItemInit.instruction);
                        newStack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound newTag = newStack.getTagCompound();

                        if (newTag != null) {
                            newTag.setTag("pages", tag.getTagList("pages", Constants.NBT.TAG_STRING));
                            newTag.setString("author", player.getName());
                            newTag.setString("title", tag.getString("title"));
                        }

                        player.setHeldItem(EnumHand.MAIN_HAND, newStack);
                    }
                }
            });
            return null;
        }
    }
}

