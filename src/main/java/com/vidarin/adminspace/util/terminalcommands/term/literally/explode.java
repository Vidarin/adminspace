package com.vidarin.adminspace.util.terminalcommands.term.literally;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class explode { //This is a joke (no shit)
    public explode(TerminalCommandHandler commandHandler, String commandArgs) {
        World world = commandHandler.getWorld();
        TileEntityTerminal terminal = commandHandler.getTerminal();
        BlockPos pos = terminal.getPos();

        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 20, true);
    }
}
