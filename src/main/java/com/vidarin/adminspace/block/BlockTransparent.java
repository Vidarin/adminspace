package com.vidarin.adminspace.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
    public @Nonnull BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = access.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();
        boolean result = blockState == iblockstate || block == this;

        return !result;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
