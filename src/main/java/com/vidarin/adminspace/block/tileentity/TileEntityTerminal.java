package com.vidarin.adminspace.block.tileentity;

import com.vidarin.adminspace.inventory.container.ContainerDummy;
import com.vidarin.adminspace.inventory.gui.GuiTerminal;
import com.vidarin.adminspace.inventory.IGuiProvider;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TileEntityTerminal extends TileEntity implements IGuiProvider {
    protected final TerminalCommandHandler commandHandler = new TerminalCommandHandler() {
        @Override
        public void setCommandStored(String command) {
            super.setCommandStored(command);
            TileEntityTerminal.this.markDirty();
        }
    };

    public int permLevel = 1;

    public TerminalCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setPermLevel(int mainIn) {
        this.permLevel = mainIn;
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.commandHandler.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.commandHandler.readDataFromNBT(compound);
        NBTTagCompound commandHandlerCompound = this.commandHandler.writeToNBT(compound);
        this.commandHandler.setCommandStored(commandHandlerCompound.getString("Command"));
    }

    @Override
    public @NotNull GuiScreen getGui(EntityPlayer player, World world, BlockPos pos) {
        return new GuiTerminal(this, player);
    }

    @Override
    public @NotNull Container getContainer(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerDummy(true);
    }
}
