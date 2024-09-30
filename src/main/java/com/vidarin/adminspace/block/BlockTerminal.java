package com.vidarin.adminspace.block;

import com.vidarin.adminspace.gui.GuiNums;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.registers.BlockRegister;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTerminal extends BlockBase {
    public static final PropertyBool IS_MAIN = PropertyBool.create("isMain");

    public BlockTerminal() {
        super("terminal", Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(IS_MAIN, false));
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

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_MAIN);
    }
}