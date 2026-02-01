package com.vidarin.adminspace.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.Nonnull;

public class ContainerDummy extends Container {
    private final boolean canInteract;

    public ContainerDummy(boolean canInteract) {
        super();
        this.canInteract = canInteract;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return this.canInteract;
    }
}
