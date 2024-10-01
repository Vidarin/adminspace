package com.vidarin.adminspace.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;

public class BlockTransparent extends BlockBase{
    public BlockTransparent(String name) {
        this(name, Material.GLASS);
    }

    public BlockTransparent(String name, Material material) {
        this(name, material, null);
    }

    public BlockTransparent(String name, Material material, CreativeTabs tab) {
        super(name, material, tab);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
