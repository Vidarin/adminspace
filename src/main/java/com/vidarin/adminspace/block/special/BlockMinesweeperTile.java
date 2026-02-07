package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.BlockBase;
import com.vidarin.adminspace.block.tileentity.TileEntityMinesweeperLogic;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.Fonts;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

@ParametersAreNonnullByDefault
public class BlockMinesweeperTile extends BlockBase implements ITileEntityProvider {
    public BlockMinesweeperTile(String name) {
        super(name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMinesweeperLogic();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
                if (logic.isFrozen()) return true;
                BlockPos center = logic.getCenter();
                switch (logic.getValue()) {
                    case 5:
                        worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                        tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            logic = (TileEntityMinesweeperLogic) tileEntity;
                            logic.setCenter(center);
                            logic.setValue(1);
                        }
                        break;
                    case 6:
                        worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                        tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            logic = (TileEntityMinesweeperLogic) tileEntity;
                            logic.setCenter(center);
                            logic.setValue(2);
                        }
                        break;
                    case 4:
                        worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                        tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            logic = (TileEntityMinesweeperLogic) tileEntity;
                            logic.setCenter(pos);
                            logic.setValue(1);
                            ((BlockMinesweeperButton) worldIn.getBlockState(pos).getBlock()).start(pos, pos, worldIn, new Random(worldIn.getSeed()), new HashSet<>(Collections.singleton(pos)));
                            playerIn.sendMessage(new TextComponentString(Fonts.Green + "Minesweeper Active!"));
                        }
                }
            }
        }
        return true;
    }
}
