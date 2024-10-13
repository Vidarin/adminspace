package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class BlockLamp extends Block {
    public BlockLamp(String name) {
        this(name, Material.REDSTONE_LIGHT);
    }

    public BlockLamp(String name, Material material) {
        this(name, material, null);
    }

    public BlockLamp(String name, Material material, CreativeTabs tab) {
        super(material);
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setLightLevel(1.0f);
        this.setSoundType(SoundType.GLASS);
        this.setCreativeTab(tab);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 15;
    }
}
