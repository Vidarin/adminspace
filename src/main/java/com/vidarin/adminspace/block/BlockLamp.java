package com.vidarin.adminspace.block;

import com.vidarin.adminspace.registers.BlockRegister;
import com.vidarin.adminspace.registers.ItemRegister;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

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

        BlockRegister.BLOCKS.add(this);
        ItemRegister.ITEMS.add((Item) new ItemBlock((Block) this).setRegistryName(this.getRegistryName()));
    }
}
