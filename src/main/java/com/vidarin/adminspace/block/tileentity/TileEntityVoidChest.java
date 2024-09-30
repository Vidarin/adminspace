package com.vidarin.adminspace.block.tileentity;

import com.vidarin.adminspace.gui.containers.ContainerVoidChest;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.registers.SoundRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityVoidChest extends TileEntityLockableLoot implements ITickable {
    private NonNullList<ItemStack> content = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
    public int numUsers;
    public int ticksSinceSync;
    public float lidAngle;
    public float prevLidAngle;

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.content) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.void_chest";
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.content = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (!this.checkLootAndRead(compound))
            ItemStackHelper.loadAllItems(compound, content);

        if (compound.hasKey("CustomName", 8))
            this.customName = compound.getString("CustomName");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (!this.checkLootAndWrite(compound))
            ItemStackHelper.saveAllItems(compound, content);

        if (compound.hasKey("CustomName", 8))
            compound.setString("CustomName", this.customName);

        return compound;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInv, EntityPlayer player) {
        return new ContainerVoidChest(playerInv, this, player);
    }

    @Override
    public String getGuiID() {
        return Adminspace.MODID + ":void_chest";
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.content;
    }

    @Override
    public void update()
    {
        if (!this.world.isRemote && this.numUsers != 0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
        {
            this.numUsers = 0;
            float f = 5.0F;

            for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)pos.getX() - 5.0F), (double)((float)pos.getY() - 5.0F), (double)((float)pos.getZ() - 5.0F), (double)((float)(pos.getX() + 1) + 5.0F), (double)((float)(pos.getY() + 1) + 5.0F), (double)((float)(pos.getZ() + 1) + 5.0F))))
            {
                if (entityplayer.openContainer instanceof ContainerVoidChest)
                {
                    if (((ContainerVoidChest)entityplayer.openContainer).getChest() == this)
                    {
                        ++this.numUsers;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        float f1 = 0.1F;

        if (this.numUsers > 0 && this.lidAngle == 0.0F)
        {
            double d1 = (double)pos.getX() + 0.5D;
            double d2 = (double)pos.getZ() + 0.5D;
            this.world.playSound((EntityPlayer)null, d1, (double)pos.getY() + 0.5D, d2, SoundRegister.VOID_DOOR_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numUsers == 0 && this.lidAngle > 0.0F || this.numUsers > 0 && this.lidAngle < 1.0F)
        {
            float f2 = this.lidAngle;

            if (this.numUsers > 0)
            {
                this.lidAngle += 0.1F;
            }
            else
            {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            float f3 = 0.5F;

            if (this.lidAngle < 0.5F && f2 >= 0.5F)
            {
                double d3 = (double)pos.getX() + 0.5D;
                double d0 = (double)pos.getZ() + 0.5D;
                this.world.playSound((EntityPlayer)null, d3, (double)pos.getY() + 0.5D, d0, SoundRegister.VOID_DOOR_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {
        ++this.numUsers;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numUsers);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        --this.numUsers;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numUsers);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
    }
}
