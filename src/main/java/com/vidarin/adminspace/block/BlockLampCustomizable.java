package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class BlockLampCustomizable extends BlockBase {
    private final int light;

    public BlockLampCustomizable(String name, int light) {
        this(name, Material.REDSTONE_LIGHT, light);
    }

    public BlockLampCustomizable(String name, Material material, int light) {
        this(name, material, null, light);
    }

    public BlockLampCustomizable(String name, Material material, CreativeTabs tab, int light) {
        super(name, material ,tab);
        this.light = light;
        this.setLightLevel((float) light / 15);
        this.setSoundType(SoundType.GLASS);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return light;
    }
}
