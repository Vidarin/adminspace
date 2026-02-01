package com.vidarin.adminspace.inventory.container;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVoidChest extends Container {
    private final TileEntityVoidChest chest;
    private final int numRows;

    public ContainerVoidChest(InventoryPlayer playerInv, TileEntityVoidChest chest, EntityPlayer player) {
        this.chest = chest;
        this.numRows = chest.getSizeInventory() / 9;
        chest.openInventory(player);
        int i = (this.numRows - 4) * 18;

        for (int j = 0; j < this.numRows; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(chest, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int x = 0; x < 3; ++x)
        {
            for (int y = 0; y < 9; ++y)
            {
                this.addSlotToContainer(new Slot(playerInv, y + x * 9 + 9, 8 + y * 18, 102 + x * 18 + i));
            }
        }

        for (int x = 0; x < 9; ++x)
        {
            this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 160 + i));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.chest.isUsableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        chest.closeInventory(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            if (index < 27)
            {
                if (!this.mergeItemStack(slotStack, 27, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, 27, false))
            {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemStack;
    }

    public TileEntityVoidChest getChest() {
        return chest;
    }
}
