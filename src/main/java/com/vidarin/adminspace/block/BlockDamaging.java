package com.vidarin.adminspace.block;

import com.vidarin.adminspace.util.DimTP;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockDamaging extends BlockBase {
    private final int damage;
    private final DamageSource source;

    public BlockDamaging(String name, int damage, DamageSource source) {
        this(name, Material.ROCK, damage, source);
    }

    public BlockDamaging(String name, Material material, int damage, DamageSource source) {
        this(name, material, null, damage, source);
    }

    public BlockDamaging(String name, Material material, CreativeTabs tab, int damage, DamageSource source) {
        super(name, material, tab);
        this.damage = damage;
        this.source = source;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase && !entityIn.isEntityInvulnerable(source))
            entityIn.attackEntityFrom(source, damage);
        if (entityIn instanceof EntityPlayer) {
            DimTP.tpToDimension((EntityPlayer) entityIn, 21, 8, 100, 8);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
}
