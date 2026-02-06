package com.vidarin.adminspace.inventory;

import com.vidarin.adminspace.block.tileentity.TileEntityKeySlotter;
import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.inventory.container.ContainerDummy;
import com.vidarin.adminspace.inventory.gui.GuiKeySlotter;
import com.vidarin.adminspace.inventory.gui.GuiTerminal;
import com.vidarin.adminspace.inventory.container.ContainerVoidChest;
import com.vidarin.adminspace.inventory.gui.GuiVoidChest;
import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.Objects;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        return switch (id) {
            case GuiIDs.GUI_VOID_CHEST -> new ContainerVoidChest(player.inventory, (TileEntityVoidChest) Objects.requireNonNull(tileEntity), player);
            case GuiIDs.GUI_TERMINAL, GuiIDs.GUI_KEY_SLOTTER -> new ContainerDummy(true);
            default -> null;
        };
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        return switch (id) {
            case GuiIDs.GUI_TERMINAL -> new GuiTerminal((TileEntityTerminal) Objects.requireNonNull(tileEntity), player);
            case GuiIDs.GUI_VOID_CHEST -> new GuiVoidChest(player.inventory, (TileEntityVoidChest) Objects.requireNonNull(tileEntity), player);
            case GuiIDs.GUI_KEY_SLOTTER -> new GuiKeySlotter((TileEntityKeySlotter) Objects.requireNonNull(tileEntity));
            default -> null;
        };
    }
}
