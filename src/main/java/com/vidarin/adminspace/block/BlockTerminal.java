package com.vidarin.adminspace.block;

import com.vidarin.adminspace.gui.GuiNums;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.registers.BlockRegister;
import com.vidarin.adminspace.util.TerminalCommandHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTerminal extends BlockBase {
    private final TerminalCommandHandler commandHandler = new TerminalCommandHandler();

    public BlockTerminal() {
        super("terminal", Material.IRON);
    }

    public TerminalCommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(BlockRegister.terminal);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote)
            player.openGui(Adminspace.instance, GuiNums.GUI_TERMINAL, world, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }
}