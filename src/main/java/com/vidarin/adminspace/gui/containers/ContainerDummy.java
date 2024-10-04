package com.vidarin.adminspace.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.Nonnull;

public class ContainerDummy extends Container {
    private boolean canInteract = true;

    public ContainerDummy(boolean canInteract) {
        super();
        this.canInteract = canInteract;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return this.canInteract;
    }
}
