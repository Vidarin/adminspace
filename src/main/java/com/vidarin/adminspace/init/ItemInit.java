package com.vidarin.adminspace.init;

import com.vidarin.adminspace.item.ItemCatPass;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final List<Item> ITEMS;

    public static final Item catPass;

    static {
        ITEMS = new ArrayList<>();

        catPass = new ItemCatPass();
    }
}
