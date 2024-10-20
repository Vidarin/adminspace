package com.vidarin.adminspace.gui;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.gui.containers.ContainerDummy;
import com.vidarin.adminspace.gui.guis.GuiTerminal;
import com.vidarin.adminspace.gui.containers.ContainerVoidChest;
import com.vidarin.adminspace.gui.guis.GuiVoidChest;
import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.Objects;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GuiIDs.GUI_VOID_CHEST:
                return new ContainerVoidChest(player.inventory, (TileEntityVoidChest) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z))), player);
            case GuiIDs.GUI_TERMINAL:
                return new ContainerDummy(true);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GuiIDs.GUI_TERMINAL:
                return new GuiTerminal((TileEntityTerminal) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z))), player);
            case GuiIDs.GUI_VOID_CHEST:
                return new GuiVoidChest(player.inventory, (TileEntityVoidChest) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z))), player);
        }
        return null;
    }
}
