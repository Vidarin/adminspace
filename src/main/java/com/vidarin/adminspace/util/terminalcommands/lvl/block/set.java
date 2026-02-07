package com.vidarin.adminspace.util.terminalcommands.lvl.block;

import com.vidarin.adminspace.block.special.BlockTerminal;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import com.vidarin.adminspace.util.terminalcommands.TermUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

@SuppressWarnings("unused")
public class set {
    public static void execute(TerminalCommandHandler commandHandler, String[] args) {
        World world = commandHandler.getWorld();

        if (TermUtil.checkPerms(commandHandler, BlockTerminal.PERM_LEVEL_MAIN)) {
            if (args.length < 4) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            String id = args[0];

            double x;
            try {
                x = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            double y;
            try {
                y = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            double z;
            try {
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                TermUtil.argumentError(commandHandler);
                return;
            }

            IBlockState state;
            if (Block.getBlockFromName(id) != null)
                state = Objects.requireNonNull(Block.getBlockFromName(id)).getDefaultState();
            else return;

            BlockPos pos = new BlockPos(x, y, z);

            if (!world.isBlockLoaded(pos)) return;

            world.setBlockState(pos, state, 3);
            world.notifyNeighborsRespectDebug(pos, state.getBlock(), false);
        }
    }
}
