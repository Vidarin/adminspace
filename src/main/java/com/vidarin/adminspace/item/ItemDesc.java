package com.vidarin.adminspace.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemDesc extends ItemBase{
    private final String[] tooltips;

    public ItemDesc(String name, CreativeTabs tab, String tooltip) {
        super(name, tab);
        this.tooltips = tooltip.split("\n");
    }

    @SideOnly(Side.CLIENT)
    public void addInfo(ItemStack stack, @Nullable World world, List<String> tooltip, final ITooltipFlag flag) {
        tooltip.addAll(Arrays.asList(this.tooltips));
    }
}
