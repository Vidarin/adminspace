package com.vidarin.adminspace.init;

import com.vidarin.adminspace.item.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final List<Item> ITEMS;

    public static final Item noiseGem;
    public static final Item rainbowGem;

    public static final Item catPass;

    public static final Item recordCalm5;
    public static final Item recordCalm6;
    public static final Item recordElsewhere;
    public static final Item recordMoonwalk;

    public static final Item accept;
    public static final Item deny;

    public static final Item instruction;

    public static final Item voidKey;
    public static final Item serverDrive;

    public static final Item repeller;
    public static final Item dismantler;

    static {
        ITEMS = new ArrayList<>();

        noiseGem = new ItemBase("noise_gem", CreativeTabs.MATERIALS);
        rainbowGem = new ItemBase("rainbow_gem");

        catPass = new ItemCatPass();

        recordCalm5 = new ItemSpecialRecord("calm_5", "? - Calm 5", SoundInit.RECORD_CALM_5);
        recordCalm6 = new ItemSpecialRecord("calm_6", "ꕚ - Calm 6", SoundInit.RECORD_CALM_6);
        recordElsewhere = new ItemSpecialRecord("elsewhere", "⁑ - Elsewhere", SoundInit.RECORD_ELSEWHERE);
        recordMoonwalk = new ItemSpecialRecord("moonwalk", "କ◌େହ◌ି ନ◌ୁହ - Moonwalk", SoundInit.RECORD_MOONWALK);

        accept = new ItemBase("accept");
        deny = new ItemBase("deny");

        instruction = new ItemInstruction();

        voidKey = new ItemVoidKey();
        serverDrive = new ItemBase("server_drive");

        repeller = new ItemRepeller();
        dismantler = new ItemDismantler();
    }
}
