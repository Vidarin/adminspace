package com.vidarin.adminspace.network;

import com.vidarin.adminspace.init.ItemInit;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class CompleteInstructionPacket implements IMessage {

    private ItemStack bookStack;

    public CompleteInstructionPacket() {}

    public CompleteInstructionPacket(ItemStack bookStack) {
        this.bookStack = bookStack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeItemStack(bookStack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            this.bookStack = packetBuffer.readItemStack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Handler implements IMessageHandler<CompleteInstructionPacket, IMessage> {

        @Override
        public IMessage onMessage(CompleteInstructionPacket message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                ItemStack bookStack = message.bookStack;
                if (bookStack.getItem() == ItemInit.instruction) {
                    NBTTagCompound bookTag = bookStack.getTagCompound();

                    if (bookTag != null && bookTag.hasKey("pages", 8)) {
                        ItemStack signedBook = new ItemStack(ItemInit.instruction);
                        NBTTagCompound signedTag = new NBTTagCompound();

                        signedTag.setTag("pages", bookTag.getTagList("pages", 8));
                        signedTag.setString("author", ctx.getServerHandler().player.getName());
                        signedTag.setString("title", bookTag.getString("title"));

                        signedBook.setTagCompound(signedTag);

                        ctx.getServerHandler().player.setHeldItem(EnumHand.MAIN_HAND, signedBook);
                    }
                }
            });
            return null;
        }
    }
}

