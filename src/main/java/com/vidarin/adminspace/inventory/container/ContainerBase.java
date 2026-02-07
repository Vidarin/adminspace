package com.vidarin.adminspace.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class ContainerBase extends Container {
    private final int slotCount;

    public ContainerBase(int slotCount) {
        this.slotCount = slotCount;
    }

    public void addPlayerInventory(InventoryPlayer playerInv, int xOff, int yOff) {
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, xOff + x * 18, yOff + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x)
        {
            this.addSlotToContainer(new Slot(playerInv, x, xOff + x * 18, yOff + 58));
        }
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (index < slotCount) {
                if (!this.mergeItemStack(stack, slotCount, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(stack, 0, slotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
        }

        return result;
    }
}
