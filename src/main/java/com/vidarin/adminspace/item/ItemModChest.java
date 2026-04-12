package com.vidarin.adminspace.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ItemModChest<T extends TileEntity> extends ItemBlock {
    public ItemModChest(Block block, Supplier<T> teSupplier) {
        super(block);
        this.setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            T te;

            @Override
            public void renderByItem(@NotNull ItemStack itemStackIn, float partialTicks) {
                if (te == null) te = teSupplier.get();
                TileEntityRendererDispatcher.instance.render(te, 0D, 0D, 0D, 0F, partialTicks);
            }
        });
    }
}
