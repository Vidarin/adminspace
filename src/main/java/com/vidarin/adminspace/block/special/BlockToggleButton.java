package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.BlockBase;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BlockToggleButton extends BlockBase {
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockToggleButton() {
        super("toggle_button");
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY)
    {
        if (!worldIn.isRemote) {
            worldIn.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, state.getValue(POWERED) ? 0.8f : 1.0f);
            worldIn.setBlockState(pos, state.cycleProperty(POWERED), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
        }
        return true;
    }

    @Override
    protected @NotNull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWERED, meta > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }
}
