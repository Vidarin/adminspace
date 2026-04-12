package com.vidarin.adminspace.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

public class BlockLamp extends BlockBase {
    private final int light;

    public BlockLamp(String name, int light) {
        this(name, Material.REDSTONE_LIGHT, light);
    }

    public BlockLamp(String name, Material material, int light) {
        this(name, material, null, light);
    }

    public BlockLamp(String name, Material material, @Nullable CreativeTabs tab, int light) {
        this(name, material, tab, light, SoundType.GLASS);
    }

    public BlockLamp(String name, Material material, @Nullable CreativeTabs tab, int light, SoundType soundType) {
        super(name, material, tab);
        this.light = light;
        this.setLightLevel((float) light / 15);
        this.setSoundType(soundType);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return light;
    }
}
