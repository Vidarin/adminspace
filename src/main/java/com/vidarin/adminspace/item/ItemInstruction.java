package com.vidarin.adminspace.item;

import com.vidarin.adminspace.inventory.gui.GuiInstruction;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.CPacketOpenInstruction;
import com.vidarin.adminspace.util.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
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
    public @Nonnull ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);

        if (!worldIn.isRemote) openServer(playerIn, stack, hand);
        else openClient(playerIn, stack);

        playerIn.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public void openServer(EntityPlayer playerIn, ItemStack stack, EnumHand hand) {
        EntityPlayerMP player = (EntityPlayerMP) playerIn;

        Item item = stack.getItem();

        if (item == ItemInit.instruction) {
            AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketOpenInstruction(hand), player);
        }
    }

    public void openClient(EntityPlayer playerIn, ItemStack stack) {
        EntityPlayerSP player = (EntityPlayerSP) playerIn;

        Item item = stack.getItem();

        if (item == ItemInit.instruction) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiInstruction(player, stack));
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public @Nonnull String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            String title = compound.getString("title");

            if (!StringUtils.isNullOrEmpty(title)) return title;
        }

        return super.getItemStackDisplayName(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("DataFlowIssue")
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            String author = compound.getString("author");

            if (!StringUtils.isNullOrEmpty(author)) tooltip.add(Fonts.Gray + I18n.format("book.byAuthor", author));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
