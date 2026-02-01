package com.vidarin.adminspace.item;

import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    public ItemBase(String name) {
        this(name, null);
    }

    public ItemBase(String name, CreativeTabs tab) {
        setTranslationKey(name);
        setRegistryName(name);
        if (tab != null)
            setCreativeTab(tab);

        ItemInit.ITEMS.add(this);
    }
}
