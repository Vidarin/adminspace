package com.vidarin.adminspace.init;

import com.vidarin.adminspace.item.ItemCatPass;
import com.vidarin.adminspace.item.ItemSpecialRecord;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final List<Item> ITEMS;

    public static final Item catPass;

    public static final Item recordCalm5;

    static {
        ITEMS = new ArrayList<>();

        catPass = new ItemCatPass();

        recordCalm5 = new ItemSpecialRecord("calm_5", SoundInit.RECORD_CALM_5);
    }
}
