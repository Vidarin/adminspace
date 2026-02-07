package com.vidarin.adminspace.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface IGuiProvider {
    @NotNull GuiScreen getGui(EntityPlayer player, World world, BlockPos pos);

    @NotNull Container getContainer(EntityPlayer player, World world, BlockPos pos);
}
