package com.vidarin.adminspace.block;

import com.vidarin.adminspace.util.DimTP;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockTeleporter extends BlockBase {
    private final int dimension;

    public BlockTeleporter(String name, int dimension) {
        super(name, Material.ROCK, CreativeTabs.MISC);
        this.dimension = dimension;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof EntityPlayer)
            DimTP.tpToDimension(((EntityPlayer) entityIn), dimension, 8, 100, 8);
    }
}
