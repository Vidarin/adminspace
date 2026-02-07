package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.BlockBase;
import com.vidarin.adminspace.block.tileentity.TileEntityMinesweeperLogic;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.Fonts;
import com.vidarin.adminspace.util.MathUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class BlockMinesweeperButton extends BlockBase implements ITileEntityProvider {
    public BlockMinesweeperButton() {
        super("minesweeper_button");
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
                if (logic.getCenter() == null) {
                    if (logic.isFrozen()) logic.unfreeze();
                    logic.setCenter(pos);
                    logic.setValue(1);
                    start(pos, pos, worldIn, new Random(worldIn.getSeed()), new HashSet<>(Collections.singleton(pos)));
                    playerIn.sendMessage(new TextComponentString(Fonts.Green + "Minesweeper Active!"));
                }
                else {
                    if (logic.isFrozen()) return true;
                    BlockPos center = logic.getCenter();
                    switch (logic.getValue()) {
                        case 1:
                            worldIn.setBlockState(pos, BlockInit.minesweeperFlag.getDefaultState(), 3);
                            tileEntity = worldIn.getTileEntity(pos);
                            if (tileEntity instanceof TileEntityMinesweeperLogic) {
                                logic = (TileEntityMinesweeperLogic) tileEntity;
                                logic.setCenter(center);
                                logic.setValue(5);
                            }
                            break;
                        case 2:
                            worldIn.setBlockState(pos, BlockInit.minesweeperFlag.getDefaultState(), 3);
                            tileEntity = worldIn.getTileEntity(pos);
                            if (tileEntity instanceof TileEntityMinesweeperLogic) {
                                logic = (TileEntityMinesweeperLogic) tileEntity;
                                logic.setCenter(center);
                                logic.setValue(6);
                            }
                            WinCheckResult result = new WinCheckResult();
                            checkWin(pos, worldIn, new HashSet<>(), result);
                            if (result.allMinesFlagged) {
                                worldIn.setBlockState(pos, BlockInit.minesweeperButton.getDefaultState(), 3);
                                tileEntity = worldIn.getTileEntity(pos);
                                if (tileEntity instanceof TileEntityMinesweeperLogic) {
                                    logic = (TileEntityMinesweeperLogic) tileEntity;
                                    logic.setCenter(center);
                                    logic.setValue(1);
                                    start(pos, pos, worldIn, new Random(worldIn.getSeed()), new HashSet<>(Collections.singleton(pos)));
                                    playerIn.sendMessage(new TextComponentString(Fonts.Gold + "You Won! :)"));
                                }
                            }
                            break;
                        default:
                            Adminspace.LOGGER.log(Level.WARN, "(onBlockActivated) Minesweeper button thought it was something else! {}", logic.getValue());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
                if (logic.getCenter() != null && !logic.isFrozen()) {
                    switch (logic.getValue()) {
                        case 1:
                            open(pos, worldIn);
                            break;
                        case 2:
                            gameOver(pos, worldIn);
                            break;
                        default:
                            Adminspace.LOGGER.log(Level.WARN, "(onBlockClicked) Minesweeper button thought it was something else! {}", logic.getValue());
                    }
                }
            }
        }
    }

    private void open(BlockPos startPos, World world) {
        if (!world.isRemote) {
            int adjacentMines = 0;
            for (BlockPos offset : MathUtil.DIRECTIONS) {
                BlockPos newPos = startPos.add(offset);
                if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperTile || world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperButton) {
                    TileEntity tileEntity = world.getTileEntity(newPos);
                    if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
                        if (logic.getValue() == 2 || logic.getValue() == 4 || logic.getValue() == 6) {
                            ++adjacentMines;
                        }
                    }
                }
            }
            if (adjacentMines == 0) {
                TileEntity tileEntity = world.getTileEntity(startPos);
                if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
                    BlockPos center = logic.getCenter();
                    world.setBlockState(startPos, BlockInit.minesweeper0.getDefaultState(), 3);
                    tileEntity = world.getTileEntity(startPos);
                    if (tileEntity instanceof TileEntityMinesweeperLogic) {
                        logic = (TileEntityMinesweeperLogic) tileEntity;
                        logic.setCenter(center);
                        logic.setValue(3);
                    }
                }
                for (BlockPos offset : MathUtil.DIRECTIONS) {
                    BlockPos newPos = startPos.add(offset);
                    if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperTile || world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperButton) {
                        tileEntity = world.getTileEntity(newPos);
                        if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
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
                        Adminspace.LOGGER.log(Level.WARN, "Tile got invalid amount of adjacent mines!");
                        break;
                }
                TileEntity tileEntity = world.getTileEntity(startPos);
                if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
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

    protected void start(BlockPos centerPos, BlockPos startPos, World world, Random rand, Set<BlockPos> visited) {
        if (!world.isRemote) {
            for (BlockPos offset : MathUtil.DIRECTIONS) {
                BlockPos newPos = startPos.add(offset);
                if (visited.contains(newPos)) continue;
                if (world.getBlockState(newPos).getBlock() instanceof BlockMinesweeperTile)
                    world.setBlockState(newPos, BlockInit.minesweeperButton.getDefaultState(), 3);
                TileEntity tileEntity = world.getTileEntity(newPos);
                if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
                    logic.setCenter(centerPos);
                    if (logic.isFrozen()) logic.unfreeze();
                    if (rand.nextInt(10) == 0 && centerPos != newPos) logic.setValue(2);
                    else logic.setValue(1);
                    visited.add(newPos);
                    start(centerPos, newPos, world, rand, visited);
                }
            }
        }
    }

    private void gameOver(BlockPos pos, World world) {
        world.setBlockState(pos, BlockInit.minesweeperMineCritical.getDefaultState(), 3);
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, true);
        for (BlockPos offset : MathUtil.DIRECTIONS) {
            BlockPos newPos = pos.add(offset);
            revealMines(newPos, world, new HashSet<>(Collections.singletonList(pos)));
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
            logic.setValue(4);
        }
    }

    private void revealMines(BlockPos pos, World world, Set<BlockPos> visited) {
        for (BlockPos offset : MathUtil.DIRECTIONS) {
            BlockPos newPos = pos.add(offset);
            if (visited.contains(newPos)) continue;
            TileEntity tileEntity = world.getTileEntity(newPos);
            if (tileEntity instanceof TileEntityMinesweeperLogic logic) {
                if (logic.getValue() % 2 == 0) {
                    world.setBlockState(newPos, BlockInit.minesweeperMine.getDefaultState(), 3);
                    logic.setValue(4);
                }
                else logic.freeze();
                visited.add(newPos);
                revealMines(newPos, world, visited);
            }
        }
    }

    private void checkWin(BlockPos pos, World world, Set<BlockPos> visited, WinCheckResult result) {
        if (!visited.add(pos)) return;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMinesweeperLogic logic) {

            if (logic.getValue() == 2 || logic.getValue() == 4 || logic.getValue() == 5) {
                result.allMinesFlagged = false;
            }

            for (BlockPos offset : MathUtil.DIRECTIONS) {
                checkWin(pos.add(offset), world, visited, result);
            }
        }
    }

    private static class WinCheckResult {
        private boolean allMinesFlagged = true;
    }
}
