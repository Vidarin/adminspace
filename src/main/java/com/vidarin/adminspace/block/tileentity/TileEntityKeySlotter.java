package com.vidarin.adminspace.block.tileentity;

import com.vidarin.adminspace.inventory.container.ContainerDummy;
import com.vidarin.adminspace.inventory.gui.GuiKeySlotter;
import com.vidarin.adminspace.inventory.GuiProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TileEntityKeySlotter extends SyncedTileEntity implements GuiProvider {
    private boolean hasKey = false;

    public void setHasKey(boolean v) {
        hasKey = v;
        markDirty();

        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    public boolean hasKey() {
        return hasKey;
    }

    @Override
    public @NotNull NBTTagCompound writeNBT(@NotNull NBTTagCompound compound) {
        compound.setBoolean("hasKey", this.hasKey);
        return compound;
    }

    @Override
    public void readNBT(@NotNull NBTTagCompound compound) {
        this.hasKey = compound.getBoolean("hasKey");
    }

    @Override
    public @NotNull GuiScreen getGui(EntityPlayer player, World world, BlockPos pos) {
        return new GuiKeySlotter(this);
    }

    @Override
    public @NotNull Container getContainer(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerDummy();
    }
}
