package com.vidarin.adminspace.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;

import javax.annotation.Nullable;

public class BlockTranslucent extends BlockTransparent {
    public BlockTranslucent(String name) {
        this(name, Material.GLASS);
    }

    public BlockTranslucent(String name, Material material) {
        this(name, material, null);
    }

    public BlockTranslucent(String name, Material material, @Nullable CreativeTabs tab) {
        super(name, material, tab);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
