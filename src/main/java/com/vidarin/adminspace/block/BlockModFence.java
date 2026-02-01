package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

import java.util.Objects;

public class BlockModFence extends BlockFence {
    public BlockModFence(String name) {
        this(name, Material.ROCK);
    }

    public BlockModFence(String name, Material material) {
        this(name, material, null);
    }

    public BlockModFence(String name, Material material, CreativeTabs tab) {
        super(material, MapColor.AIR);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(tab);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(name));
    }
}
