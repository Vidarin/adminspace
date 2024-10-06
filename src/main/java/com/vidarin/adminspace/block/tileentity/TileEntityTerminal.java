package com.vidarin.adminspace.block.tileentity;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class TileEntityTerminal extends TileEntity {
    private final TerminalCommandHandler commandHandler = new TerminalCommandHandler() {
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
    public void validate() {
        this.blockType = null;
        super.validate();
    }
}
