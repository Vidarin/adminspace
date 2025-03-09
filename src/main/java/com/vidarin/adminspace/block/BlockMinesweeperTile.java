package com.vidarin.adminspace.block;

import com.vidarin.adminspace.block.tileentity.TileEntityMinesweeperLogic;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.Fonts;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockMinesweeperTile extends BlockBase implements ITileEntityProvider {
    public BlockMinesweeperTile(String name) {
        super(name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityMinesweeperLogic();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMinesweeperLogic) {
                TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                BlockPos center = logic.getCenter();
                switch (logic.getValue()) {
                    case 5:
                        worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                        tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            logic = (TileEntityMinesweeperLogic) tileEntity;
                            logic.setCenter(center);
                            logic.setValue(1);
                            playerIn.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        }
                        break;
                    case 6:
                        worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                        tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            logic = (TileEntityMinesweeperLogic) tileEntity;
                            logic.setCenter(center);
                            logic.setValue(2);
                            playerIn.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        }
                        break;
                    case 4:
                        worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                        tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            logic = (TileEntityMinesweeperLogic) tileEntity;
                            logic.setCenter(pos);
                            logic.setValue(1);
                            ((BlockMinesweeperButton) worldIn.getBlockState(pos).getBlock()).start(pos, pos, worldIn);
                            playerIn.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);
                            playerIn.sendMessage(new TextComponentString(Fonts.Green + "Minesweeper Active!"));
                        }
                }
            }
        }
        return false;
    }
}
