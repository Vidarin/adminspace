package com.vidarin.adminspace.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockCustomDrop extends BlockBase {
    private final Item drop;
    private final int quantity1, quantity2;

    public BlockCustomDrop(String name, Item drop, int quantity1, int quantity2) {
        this(name, Material.ROCK, drop, quantity1, quantity2);
    }

    public BlockCustomDrop(String name, Material material, Item drop, int quantity1, int quantity2) {
        this(name, material, null, drop, quantity1, quantity2);
    }

    public BlockCustomDrop(String name, Material material, CreativeTabs tab, Item drop, int quantity1, int quantity2) {
        super(name, material, tab);

        this.drop = drop;
        this.quantity1 = quantity1;
        this.quantity2 = quantity2;

        this.setHardness(3f);
        this.setResistance(10f);
        this.setHarvestLevel("pickaxe", 3);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return drop;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return random.nextInt(quantity2 - quantity1) + quantity1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        if (quantity2 > 1)
            return random.nextInt(quantity2 - quantity1) + quantity1 + random.nextInt(fortune);
        else
            return 1;
    }
}
