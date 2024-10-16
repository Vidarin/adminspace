package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockToggleButtonOff extends BlockBase {
    public BlockToggleButtonOff() {
        super("toggle_button_off");
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY)
    {
        if (!worldIn.isRemote) {
            worldIn.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            worldIn.setBlockState(pos, BlockInit.toggleButtonOn.getDefaultState(), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
        }
        return true;
    }
}
