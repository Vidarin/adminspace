package com.vidarin.adminspace.util.terminalcommands.player.inv;

import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermError;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class set {
    public set(TerminalCommandHandler commandHandler, String commandArgs) {
        EntityPlayer player = commandHandler.getPlayer();
        InventoryPlayer inventory = player.inventory;

        TermError termError = new TermError(commandHandler, commandArgs);

        try {
            String id = commandArgs.split("/")[0];
            int slot;
            int amount;
            try {
                slot = Integer.parseInt(commandArgs.split("/")[1]);
                amount = Integer.parseInt(commandArgs.split("/")[2]);
            } catch (NumberFormatException e) {
                slot = 0;
                amount = 0;
                termError.argumentError(commandHandler);
            }

            if (amount > 64) amount = 64;

            if (slot > 45 || slot < 5) slot = 9;


            Item item = Item.getByNameOrId(id);

            assert item != null;
            ItemStack stack = new ItemStack(item, amount);

            inventory.setInventorySlotContents(slot, stack);
        } catch (ArrayIndexOutOfBoundsException e) {
            termError.argumentError(commandHandler);
        }
    }
}
