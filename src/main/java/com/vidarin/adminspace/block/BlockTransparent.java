package com.vidarin.adminspace.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockTransparent extends BlockBase {
    public boolean alwaysRenderSides = false;

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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public BlockTransparent alwaysRenderSides() {
        this.alwaysRenderSides = true;
        return this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        if (alwaysRenderSides) return true;

        IBlockState iblockstate = access.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();
        boolean result = blockState == iblockstate || block == this;

        return !result;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
