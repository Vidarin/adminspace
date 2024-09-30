package com.vidarin.adminspace.item;

import com.vidarin.adminspace.registers.ItemRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    public ItemBase(String name, CreativeTabs tab) {
        setUnlocalizedName(name);
        setRegistryName(name);
        if (tab != null)
            setCreativeTab(tab);

        ItemRegister.ITEMS.add(this);
    }
}
