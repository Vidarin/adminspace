package com.vidarin.adminspace.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class BlockColor extends BlockLamp implements IBlockColor {
    public static final List<BlockColor> ALL_COLOR_BLOCKS = new ArrayList<>();

    public final BiFunction<IBlockAccess, BlockPos, Integer> colorSupplier;
    private final int light;

    public BlockColor(String name, Material material, @Nullable CreativeTabs tab, int color, int light) {
        this(name, material, tab, (w, p) -> color, light);
    }

    public BlockColor(String name, Material material, @Nullable CreativeTabs tab, BiFunction<IBlockAccess, BlockPos, Integer> colorSupplier, int light) {
        super(name, material, tab, light);
        this.colorSupplier = colorSupplier;
        this.light = light;
        ALL_COLOR_BLOCKS.add(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0f;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (light == 0) return super.getPackedLightmapCoords(state, source, pos);
        else return 15 << 20 | 15 << 4; // Same number as the one from blockbuster mod
    }

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
        if (pos != null) {
            return colorSupplier.apply(worldIn, pos);
        }
        return 0xFFFFFF;
    }
}
