package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

public class BlockModLever extends BlockLever {
    public BlockModLever(String name, CreativeTabs tab) {
        super();
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(tab);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(name));
    }
}
