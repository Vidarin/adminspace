package com.vidarin.adminspace.main;

import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public final class RecipeHandler {
    public static void registerFurnaceRecipes() {
        FurnaceRecipes registry = FurnaceRecipes.instance();
        registry.addSmeltingRecipeForBlock(Blocks.BRICK_BLOCK, new ItemStack(BlockInit.hardenedBricks), 0.1f);
    }
}
