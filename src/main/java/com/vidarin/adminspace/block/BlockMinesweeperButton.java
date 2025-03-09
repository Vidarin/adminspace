package com.vidarin.adminspace.block;

import com.vidarin.adminspace.block.tileentity.TileEntityMinesweeperLogic;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.Fonts;
import mods.thecomputerizer.theimpossiblelibrary.util.file.LogUtil;
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
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class BlockMinesweeperButton extends BlockBase implements ITileEntityProvider {
    public BlockMinesweeperButton() {
        super("minesweeper_button");
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
                if (logic.getCenter() == null) {
                    logic.setCenter(pos);
                    logic.setValue(1);
                    start(pos, pos, worldIn);
                    playerIn.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);
                    playerIn.sendMessage(new TextComponentString(Fonts.Green + "Minesweeper Active!"));
                }
                else {
                    BlockPos center = logic.getCenter();
                    switch (logic.getValue()) {
                        case 1:
                            worldIn.setBlockState(pos, BlockInit.minesweeperFlag.getDefaultState(), 3);
                            tileEntity = worldIn.getTileEntity(pos);
                            if (tileEntity instanceof TileEntityMinesweeperLogic) {
                                logic = (TileEntityMinesweeperLogic) tileEntity;
                                logic.setCenter(center);
                                logic.setValue(5);
                                playerIn.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            }
                            break;
                        case 2:
                            worldIn.setBlockState(pos, BlockInit.minesweeperFlag.getDefaultState(), 3);
                            tileEntity = worldIn.getTileEntity(pos);
                            if (tileEntity instanceof TileEntityMinesweeperLogic) {
                                logic = (TileEntityMinesweeperLogic) tileEntity;
                                logic.setCenter(center);
                                logic.setValue(6);
                                playerIn.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            }
                            break;
                        default:
                            LogUtil.logInternal(Level.WARN, "(onBlockActivated) Minesweeper button thought it was something else!" + logic.getValue());
                            return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMinesweeperLogic) {
                TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                if (logic.getCenter() != null) {
                    switch (logic.getValue()) {
                        case 1:
                            open(pos, worldIn);
                            break;
                        case 2:
                            gameOver(pos, worldIn);
                            break;
                        default:
                            LogUtil.logInternal(Level.WARN, "(onBlockClicked) Minesweeper button thought it was something else!" + logic.getValue());
                    }
                }
            }
        }
    }

    private void open(BlockPos startPos, World world) {
        if (!world.isRemote) {
            int adjacentMines = 0;
            for (int i = 0; i < 8; i++) {
                int dx = (int) Math.round(Math.cos(Math.toRadians(i * 45)));
                int dz = (int) Math.round(Math.sin(Math.toRadians(i * 45)));
                BlockPos newPos = startPos.add(dx, 0, dz);
                if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperTile || world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperButton) {
                    TileEntity tileEntity = world.getTileEntity(newPos);
                    if (tileEntity instanceof TileEntityMinesweeperLogic) {
                        TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                        if (logic.getValue() == 2 || logic.getValue() == 4 || logic.getValue() == 6) {
                            ++adjacentMines;
                        }
                    }
                }
            }
            if (adjacentMines == 0) {
                TileEntity tileEntity = world.getTileEntity(startPos);
                if (tileEntity instanceof TileEntityMinesweeperLogic) {
                    TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                    BlockPos center = logic.getCenter();
                    world.setBlockState(startPos, BlockInit.minesweeper0.getDefaultState(), 3);
                    tileEntity = world.getTileEntity(startPos);
                    if (tileEntity instanceof TileEntityMinesweeperLogic) {
                        logic = (TileEntityMinesweeperLogic) tileEntity;
                        logic.setCenter(center);
                        logic.setValue(3);
                    }
                }
                for (int i = 0; i < 8; i++) {
                    int dx = (int) Math.round(Math.cos(Math.toRadians(i * 45)));
                    int dz = (int) Math.round(Math.sin(Math.toRadians(i * 45)));
                    BlockPos newPos = startPos.add(dx, 0, dz);
                    if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperTile || world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperButton) {
                        tileEntity = world.getTileEntity(newPos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic) {
                            TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                            if (logic.getCenter() != null) {
                                if (logic.getValue() <= 2) {
                                    open(newPos, world);
                                }
                            }
                        }
                    }
                }
            } else {
                IBlockState state;
                switch (adjacentMines) {
                    case 1:
                        state = BlockInit.minesweeper1.getDefaultState();
                        break;
                    case 2:
                        state = BlockInit.minesweeper2.getDefaultState();
                        break;
                    case 3:
                        state = BlockInit.minesweeper3.getDefaultState();
                        break;
                    case 4:
                        state = BlockInit.minesweeper4.getDefaultState();
                        break;
                    case 5:
                        state = BlockInit.minesweeper5.getDefaultState();
                        break;
                    case 6:
                        state = BlockInit.minesweeper6.getDefaultState();
                        break;
                    case 7:
                        state = BlockInit.minesweeper7.getDefaultState();
                        break;
                    case 8:
                        state = BlockInit.minesweeper8.getDefaultState();
                        break;
                    default:
                        state = BlockInit.minesweeper0.getDefaultState();
                        LogUtil.logInternal(Level.WARN, "Tile got invalid amount of adjacent mines!");
                        break;
                }
                TileEntity tileEntity = world.getTileEntity(startPos);
                if (tileEntity instanceof TileEntityMinesweeperLogic) {
                    TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                    BlockPos center = logic.getCenter();
                    world.setBlockState(startPos, state, 3);
                    tileEntity = world.getTileEntity(startPos);
                    if (tileEntity instanceof TileEntityMinesweeperLogic) {
                        logic = (TileEntityMinesweeperLogic) tileEntity;
                        logic.setCenter(center);
                        logic.setValue(3);
                    }
                }
            }
        }
    }

    protected void start(BlockPos centerPos, BlockPos startPos, World world) {
        Random rand = new Random(world.getSeed());
        if (!world.isRemote) {
            for (int i = 0; i < 8; i++) {
                int dx = (int) Math.round(Math.cos(Math.toRadians(i * 45)));
                int dz = (int) Math.round(Math.sin(Math.toRadians(i * 45)));
                BlockPos newPos = startPos.add(dx, 0, dz);
                if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperTile)
                    world.setBlockState(newPos, BlockInit.minesweeperButton.getDefaultState(), 3);
                if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperButton) {
                    TileEntity tileEntity = world.getTileEntity(newPos);
                    if (tileEntity instanceof TileEntityMinesweeperLogic) {
                        TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
                        if (logic.getCenter() == null) {
                            logic.setCenter(centerPos);
                            if (rand.nextInt(10) == 0) logic.setValue(2);
                            else logic.setValue(1);

                            start(centerPos, newPos, world);
                        }
                    }
                }
            }
        }
    }

    private void gameOver(BlockPos pos, World world) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMinesweeperLogic) {
            //TileEntityMinesweeperLogic logic = (TileEntityMinesweeperLogic) tileEntity;
            //Map<BlockPos, Integer> fieldMap = Objects.requireNonNull(logic.getFieldMap());
            //List<BlockPos> mines = new ArrayList<>();
            world.setBlockState(pos, BlockInit.minesweeperMineCritical.getDefaultState(), 3);
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, true);
            /*tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof  TileEntityMinesweeperLogic) {
                logic = (TileEntityMinesweeperLogic) tileEntity;
                logic.setValue(4);
            }
            for (Map.Entry<BlockPos, Integer> fieldEntry : fieldMap.entrySet()) {
                if (fieldEntry.getValue() % 2 == 1) {
                    mines.add(fieldEntry.getKey());
                }
            }
            for (BlockPos minePos : mines) {
                world.setBlockState(minePos, BlockInit.minesweeperMine.getDefaultState(), 3);
                world.createExplosion(null, minePos.getX(), minePos.getY(), minePos.getZ(), 2.0F, true);
            }*/
        }
    }
}
