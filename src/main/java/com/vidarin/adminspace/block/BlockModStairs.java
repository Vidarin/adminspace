package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nullable;

public class BlockModStairs extends BlockStairs {
    public BlockModStairs(String name, Block block) {
        this(name, null, block);
    }

    @SuppressWarnings("deprecation")
    public BlockModStairs(String name, @Nullable CreativeTabs tab, Block block) {
        super(block.getDefaultState());
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(block.getSoundType());
        //noinspection DataFlowIssue
        this.setCreativeTab(tab);

        this.useNeighborBrightness = true;

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(name));
    }
}