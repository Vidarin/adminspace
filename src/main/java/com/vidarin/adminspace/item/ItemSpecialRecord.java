package com.vidarin.adminspace.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemSpecialRecord extends ItemBase {
    private final String recordName;

    public ItemSpecialRecord(String name, SoundEvent soundIn)
    {
        super("record_" + name, CreativeTabs.MISC);
        this.recordName = name;
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(this.getRecordName());
    }

    @SideOnly(Side.CLIENT)
    public String getRecordName()
    {
        switch (recordName) {
            case "calm_5":
                return "? - Calm 5";
        }
        return "whoops dev forgor to add name for disc";
    }

    @Override
    public @Nonnull EnumRarity getRarity(@Nonnull ItemStack stack)
    {
        return EnumRarity.RARE;
    }

    public String getUntranslatedRecordName() {
        return recordName;
    }
}
