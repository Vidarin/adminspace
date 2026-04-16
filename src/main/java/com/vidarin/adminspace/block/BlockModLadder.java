package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import net.minecraft.item.ItemBlock;

public class BlockModLadder extends BlockLadder {
    public BlockModLadder(String name) {
        super();
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setSoundType(SoundType.METAL);
        //noinspection DataFlowIssue
        this.setCreativeTab(null);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(name));
    }
}
