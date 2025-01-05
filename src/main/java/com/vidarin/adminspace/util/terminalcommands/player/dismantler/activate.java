package com.vidarin.adminspace.util.terminalcommands.player.dismantler;

import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.item.ItemDismantler;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermError;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class activate {
    public activate(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();

        try {
            ItemStack dismantler = player.getHeldItem(EnumHand.MAIN_HAND);
            if (dismantler.getItem() instanceof ItemDismantler) {
                if (dismantler.hasTagCompound()) {
                    assert dismantler.getTagCompound() != null;
                    if (commandArgs.equals(dismantler.getTagCompound().getString("Activation_Code"))) {
                        dismantler.getTagCompound().setBoolean("Activated", true);
                        player.playSound(SoundInit.DEATH_EASTER_EGG, 2.0F, 1.0F);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            TermError.argumentError(commandHandler);
        }
    }
}
