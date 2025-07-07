package com.vidarin.adminspace.block;

import com.vidarin.adminspace.util.DimTP;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTeleporter extends BlockBase {
    private final int dimension;
    private final int y;

    public BlockTeleporter(String name, int dimension, int y) {
        super(name, Material.ROCK, CreativeTabs.MISC);
        this.dimension = dimension;
        this.y = y;
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof EntityPlayer)
            DimTP.tpToDimension(((EntityPlayer) entityIn), dimension, 8, y, 8);
    }
}
