package com.vidarin.adminspace.item;

import com.vidarin.adminspace.inventory.gui.GuiInstruction;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.util.Fonts;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class ItemInstruction extends ItemBase {
    public ItemInstruction() {
        super("instruction", null);
        this.setMaxStackSize(1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        ItemStack stack = playerIn.getHeldItem(hand);

        if (!worldIn.isRemote)
        {
            this.resolveContents(stack, playerIn);
        }

        if (!worldIn.isRemote) openBookServer(playerIn, stack, hand);
        else openBookClient(playerIn, stack);
        playerIn.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public void openBookServer(EntityPlayer playerIn, ItemStack stack, EnumHand hand) {
        EntityPlayerMP player = (EntityPlayerMP) playerIn;

        Item item = stack.getItem();

        if (item == ItemInit.instruction)
        {
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeEnumValue(hand);
            player.connection.sendPacket(new SPacketCustomPayload("MC|BOpen", packetbuffer));
        }
    }

    public void openBookClient(EntityPlayer playerIn, ItemStack stack) {
        EntityPlayerSP player = (EntityPlayerSP) playerIn;

        Item item = stack.getItem();

        if (item == ItemInit.instruction)
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiInstruction(player, stack));
        }
    }

    public static boolean isNBTInvalid(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return true;
        }
        else if (!nbt.hasKey("pages", 9))
        {
            return true;
        }
        else
        {
            NBTTagList nbttaglist = nbt.getTagList("pages", 8);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                String s = nbttaglist.getStringTagAt(i);

                if (s.length() > 32767)
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean validBookTagContents(NBTTagCompound nbt)
    {
        if (ItemInstruction.isNBTInvalid(nbt))
        {
            return false;
        }
        else if (!nbt.hasKey("title", 8))
        {
            return false;
        }
        else
        {
            String s = nbt.getString("title");
            return s.length() <= 32 && nbt.hasKey("author", 8);
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public @Nonnull String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString("title");

            if (!StringUtils.isNullOrEmpty(s))
            {
                return s;
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("DataFlowIssue")
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString("author");

            if (!StringUtils.isNullOrEmpty(s))
            {
                tooltip.add(Fonts.Gray + I18n.format("book.byAuthor", s));
            }
        }
    }

    private void resolveContents(ItemStack stack, EntityPlayer player)
    {
        if (stack.hasTagCompound() && stack.getTagCompound() != null)
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (!nbttagcompound.getBoolean("resolved"))
            {
                nbttagcompound.setBoolean("resolved", true);

                if (validBookTagContents(nbttagcompound))
                {
                    NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);

                    for (int i = 0; i < nbttaglist.tagCount(); ++i)
                    {
                        String s = nbttaglist.getStringTagAt(i);
                        ITextComponent itextcomponent;

                        try
                        {
                            itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
                            if (itextcomponent == null) itextcomponent = new TextComponentString(s);
                            else itextcomponent = TextComponentUtils.processComponent(player, itextcomponent, player);
                        }
                        catch (Exception var9)
                        {
                            itextcomponent = new TextComponentString(s);
                        }

                        nbttaglist.set(i, new NBTTagString(ITextComponent.Serializer.componentToJson(itextcomponent)));
                    }

                    nbttagcompound.setTag("pages", nbttaglist);

                    if (player instanceof EntityPlayerMP && player.getHeldItemMainhand() == stack)
                    {
                        Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        if (slot == null) return;
                        ((EntityPlayerMP)player).connection.sendPacket(new SPacketSetSlot(0, slot.slotNumber, stack));
                    }
                }
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
