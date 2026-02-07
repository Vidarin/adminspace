package com.vidarin.adminspace.item;

import com.vidarin.adminspace.util.Fonts;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemVoidKey extends ItemBase {
    public ItemVoidKey() {
        super("void_key");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayer player) {
            if (worldIn.isRemote) {
                player.sendStatusMessage(new TextComponentString(Fonts.Red + "KEY(S) HAVE BEEN FOUND IN YOUR INVENTORY. THESE ITEMS ARE ADMIN ONLY."), true);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(Fonts.Gray + I18n.format("item.void_key.desc"));
    }
}
