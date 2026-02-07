package com.vidarin.adminspace.block.tileentity;

import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.inventory.IGuiProvider;
import com.vidarin.adminspace.inventory.container.ContainerServerContainer;
import com.vidarin.adminspace.inventory.gui.GuiServerContainer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TileEntityServerContainer extends TileEntity implements IGuiProvider {
    public final ItemStackHandler inventory;

    public TileEntityServerContainer(World world) {
        this.world = world;
        this.inventory = new ItemStackHandler(10) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() == ItemInit.serverDrive;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TextComponentTranslation("container.server_container");
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    public boolean hasDrives() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).getItem() != ItemInit.serverDrive) return false;
        }

        return true;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        else return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public @NotNull Container getContainer(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerServerContainer(player.inventory, this);
    }

    @Override
    public @NotNull GuiScreen getGui(EntityPlayer player, World world, BlockPos pos) {
        return new GuiServerContainer(player.inventory, this, getContainer(player, world, pos));
    }
}
