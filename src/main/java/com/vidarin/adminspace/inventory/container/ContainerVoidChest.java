package com.vidarin.adminspace.inventory.container;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ContainerVoidChest extends ContainerBase {
    private final TileEntityVoidChest chest;

    public ContainerVoidChest(InventoryPlayer playerInv, TileEntityVoidChest chest, EntityPlayer player) {
        super(chest.getSizeInventory());
        this.chest = chest;
        int numRows = chest.getSizeInventory() / 9;
        chest.openInventory(player);
        int i = (numRows - 4) * 18;

        for (int x = 0; x < numRows; ++x)
        {
            for (int y = 0; y < 9; ++y)
            {
                this.addSlotToContainer(new Slot(chest, y + x * 9, 8 + y * 18, 18 + x * 18));
            }
        }

        this.addPlayerInventory(playerInv, 8, 102 + i);
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

    public TileEntityVoidChest getChest() {
        return chest;
    }
}
