package com.vidarin.adminspace.inventory.container;

import com.vidarin.adminspace.block.tileentity.TileEntityServerContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerServerContainer extends ContainerBase {
    private final TileEntityServerContainer serverContainer;

    public ContainerServerContainer(InventoryPlayer playerInv, TileEntityServerContainer serverContainer) {
        super(10);

        for (int i = 0; i < 5; i++) {
            this.addSlotToContainer(new SlotItemHandler(serverContainer.inventory, i * 2, 8 + i * 36, 26));
            this.addSlotToContainer(new SlotItemHandler(serverContainer.inventory, i * 2 + 1, 8 + i * 36, 62));
        }

        this.addPlayerInventory(playerInv, 8, 98);

        this.serverContainer = serverContainer;
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return serverContainer.isUsableByPlayer(playerIn);
    }
}
