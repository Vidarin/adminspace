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
    private final String trackName;
    private final SoundEvent sound;

    public ItemSpecialRecord(String registryName, String trackName, SoundEvent sound) {
        super("record_" + registryName, CreativeTabs.MISC);
        this.trackName = trackName;
        this.sound = sound;
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.getTrackName());
    }

    @SideOnly(Side.CLIENT)
    public String getTrackName() {
        return trackName;
    }

    public SoundEvent getSound() {
        return sound;
    }

    @Override
    public @Nonnull EnumRarity getRarity(@Nonnull ItemStack stack)
    {
        return EnumRarity.RARE;
    }
}
