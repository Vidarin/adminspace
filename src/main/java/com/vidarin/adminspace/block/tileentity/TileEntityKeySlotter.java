package com.vidarin.adminspace.block.tileentity;

import com.vidarin.adminspace.inventory.container.ContainerDummy;
import com.vidarin.adminspace.inventory.gui.GuiKeySlotter;
import com.vidarin.adminspace.inventory.IGuiProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TileEntityKeySlotter extends TileEntity implements IGuiProvider {
    private boolean hasKey = false;

    public TileEntityKeySlotter(World world) {
        this.world = world;
    }

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
    public @NotNull NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setBoolean("hasKey", this.hasKey);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.hasKey = tag.getBoolean("hasKey");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("hasKey", this.hasKey);
        return new SPacketUpdateTileEntity(this.pos, 0, tag);
    }

    @Override
    public void onDataPacket(@NotNull NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        this.hasKey = tag.getBoolean("hasKey");
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("hasKey", this.hasKey);
        return compound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.hasKey = compound.getBoolean("hasKey");
    }

    @Override
    public @NotNull GuiScreen getGui(EntityPlayer player, World world, BlockPos pos) {
        return new GuiKeySlotter(this);
    }

    @Override
    public @NotNull Container getContainer(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerDummy(true);
    }
}
