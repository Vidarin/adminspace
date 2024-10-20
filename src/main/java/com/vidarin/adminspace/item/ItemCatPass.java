package com.vidarin.adminspace.item;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.Fonts;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemCatPass extends ItemBase{
    public ItemCatPass() {
        super("cat_pass", null);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState blockState = worldIn.getBlockState(pos);

        if (blockState.getBlock() == BlockInit.cardReader)
        {
            if (!worldIn.isRemote)
            {
                if (worldIn.getBlockState(pos.up()) == BlockInit.sunBlock.getDefaultState() && worldIn.getBlockState(pos.down()) == BlockInit.moonBlock.getDefaultState()) {
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                    worldIn.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
                    worldIn.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
                }
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.PASS;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(Fonts.Green + "Premium access to the Cat Shrine");
    }
}
