package com.vidarin.adminspace.block.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityMinesweeperLogic extends TileEntity {
    private @Nullable BlockPos center = null;
    private int value; // -- VALUES --
                       // 1: unopened tile
                       // 2: unopened mine tile
                       // 3: opened tile
                       // 4: opened mine tile
                       // 5: incorrect flag tile
                       // 6: correct flag tile

    public @Nullable BlockPos getCenter() {
        return center;
    }

    public void setCenter(@Nullable BlockPos center) {
        this.center = center;
        markDirty();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        markDirty();
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (center != null) {
            compound.setLong("Center", center.toLong());
        }
        compound.setInteger("Value", value);
        return compound;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Center")) {
            this.center = BlockPos.fromLong(compound.getLong("Center"));
        }
        this.value = compound.getInteger("Value");
    }
}
