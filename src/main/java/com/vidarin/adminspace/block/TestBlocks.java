package com.vidarin.adminspace.block;

import com.vidarin.adminspace.block.tileentity.SyncedTileEntity;
import com.vidarin.adminspace.dimension.skysector.generator.SkySectorGenBlockDefinition;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.CubePos;
import com.vidarin.adminspace.util.Fonts;
import com.vidarin.adminspace.util.NBTSerializer;
import com.vidarin.adminspace.util.VisibilityUtil;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlock;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"DataFlowIssue", "deprecation"})
public class TestBlocks {
    public static class GenBlockTest extends BlockContainer {
        public GenBlockTest() {
            super(Material.IRON);
            this.setRegistryName("gen_block_test");
            this.setTranslationKey("gen_block_test");

            BlockInit.BLOCKS.add(this);
            ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName("gen_block_test"));
        }

        @Override
        public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
            return new TileEntityGenBlockTest();
        }

        @Override
        public EnumBlockRenderType getRenderType(IBlockState state) {
            return EnumBlockRenderType.MODEL;
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (!worldIn.isRemote) {
                TileEntityGenBlockTest te = (TileEntityGenBlockTest) worldIn.getTileEntity(pos);
                if (!te.initialized()) te.initialize(pos, worldIn);
                if (playerIn.isSneaking()) te.reset(playerIn, worldIn);
                else te.next(playerIn, worldIn);
            }
            return true;
        }

        public static class TileEntityGenBlockTest extends SyncedTileEntity {
            private GenBlock<IBlockState> genBlock;

            public void initialize(BlockPos pos, World world) {
                this.genBlock = new GenBlock<>(new Shape<>(
                        SkySectorGenBlockDefinition.Symbols.EntryPoint,
                        new Cube(new CubePos(pos.getX(), pos.getY() + 1, pos.getZ()), new CubePos(pos.getX() + 300, pos.getY() + 181, pos.getZ() + 300)),
                        new int[]{0, 0}
                ), world.rand);
            }

            public boolean initialized() {
                return genBlock != null;
            }

            public void next(EntityPlayer player, World world) {
                this.genBlock.setRand(world.rand);
                this.genBlock.applyRules(SkySectorGenBlockDefinition.RULES, Blocks.AIR.getDefaultState(), 30);
                Adminspace.LOGGER.info("[Gen Block Test] Applied rules successfully");
                this.genBlock.extractAll().forEach((x, y, z, b) -> world.setBlockState(new BlockPos(x, y, z), b == null ? Blocks.AIR.getDefaultState() : b, 2));
                player.sendMessage(new TextComponentString(Fonts.Green + "Updated"));
            }

            public void reset(EntityPlayer player, World world) {
                this.genBlock = new GenBlock<>(new Shape<>(
                        SkySectorGenBlockDefinition.Symbols.EntryPoint,
                        new Cube(new CubePos(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ()), new CubePos(this.pos.getX() + 300, this.pos.getY() + 181, this.pos.getZ() + 300)),
                        new int[]{0, 0}
                ), world.rand);
                this.genBlock.extractAll().forEach((x, y, z, b) -> world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 2));
                player.sendMessage(new TextComponentString(Fonts.Red + "Reset"));
            }

            @Override
            public NBTTagCompound writeNBT(NBTTagCompound compound) {
                if (initialized()) compound.setTag("genBlock", genBlock.toNBT(NBTSerializer.BLOCK_STATE, SkySectorGenBlockDefinition.Symbols.values()));
                return compound;
            }

            @Override
            public void readNBT(NBTTagCompound compound) {
                if (compound.hasKey("genBlock")) this.genBlock = GenBlock.fromNBT(compound.getCompoundTag("genBlock"), NBTSerializer.BLOCK_STATE, SkySectorGenBlockDefinition.Symbols.values());
            }
        }
    }

    public static class Visibility extends BlockContainer {
        public Visibility() {
            super(Material.IRON);
            this.setRegistryName("visibility_test");
            this.setTranslationKey("visibility_test");

            BlockInit.BLOCKS.add(this);
            ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName("visibility_test"));
        }

        @Override
        public EnumBlockRenderType getRenderType(IBlockState state) {
            return EnumBlockRenderType.MODEL;
        }

        @Override
        public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
            return new TileEntityVisibilityTest();
        }

        public static class TileEntityVisibilityTest extends TileEntity implements ITickable {
            public boolean flag = false;
            public boolean seen = true;

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
