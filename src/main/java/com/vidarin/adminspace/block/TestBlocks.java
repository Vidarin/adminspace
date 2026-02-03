package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.util.VisibilityUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TestBlocks {
    public static class Visibility extends BlockContainer {
        public Visibility() {
            super(Material.IRON);
            this.setRegistryName("visibility_test");
            this.setTranslationKey("visibility_test");

            BlockInit.BLOCKS.add(this);
            ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName("visibility_test"));
        }

        @Override
        public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
            return new TileEntityVisibilityTest(worldIn);
        }

        public static class TileEntityVisibilityTest extends TileEntity implements ITickable {
            public boolean flag = false;
            public boolean seen = true;

            public TileEntityVisibilityTest(World world) {
                this.world = world;
            }

            @Override
            public void update() {
                if (!world.isRemote) {
                    BlockPos pos1 = getPos().add(-5, 1, -10);
                    BlockPos pos2 = getPos().add(5, 12, 10);
                    AxisAlignedBB bb = new AxisAlignedBB(pos1, pos2);

                    if (seen) {
                        if (!VisibilityUtil.isRangeBeingObserved(world, bb, VisibilityUtil.Accuracy.Varying, 300, 90)) {
                            for (int x = pos1.getX(); x < pos2.getX(); x++) {
                                for (int y = pos1.getY(); y < pos2.getY(); y++) {
                                    for (int z = pos1.getZ(); z < pos2.getZ(); z++) {
                                        world.setBlockState(new BlockPos(x, y, z), flag ? Blocks.AIR.getDefaultState() : Blocks.DIRT.getDefaultState(), 2);
                                    }
                                }
                            }
                            flag = !flag;
                            seen = false;
                        }
                    } else {
                        if (VisibilityUtil.isRangeBeingObserved(world, bb, VisibilityUtil.Accuracy.Varying, 300, 90)) seen = true;
                    }
                }
            }

            @Override
            public NBTTagCompound writeToNBT(NBTTagCompound compound) {
                super.writeToNBT(compound);
                compound.setBoolean("flag", flag);
                compound.setBoolean("seen", seen);
                return compound;
            }

            @Override
            public void readFromNBT(NBTTagCompound compound) {
                super.readFromNBT(compound);
                this.flag = compound.getBoolean("flag");
                this.seen = compound.getBoolean("seen");
            }
        }
    }
}
