package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.SoundInit;
import net.minecraft.item.ItemDoor;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class
BlockModDoor extends BlockDoor {
    public BlockModDoor(String name) {
        this(name, Material.ROCK);
    }

    public BlockModDoor(String name, Material material) {
        this(name, material, null);
    }

    public BlockModDoor(String name, Material material, CreativeTabs tab) {
        super(material);
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(SoundType.METAL);
        if (tab != null)
            this.setCreativeTab(tab);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemDoor(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
    }

    private SoundEvent getDoorCloseSound()
    {
        return SoundInit.VOID_DOOR_CLOSE;
    }

    private SoundEvent getDoorOpenSound()
    {
        return SoundInit.VOID_DOOR_OPEN;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull Item getItemDropped(IBlockState state, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull ItemStack getPickBlock(IBlockState state, RayTraceResult traceResult, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY)
    {
        if (this.blockMaterial == Material.IRON)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
            IBlockState iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this)
            {
                return false;
            }
            else
            {
                state = iblockstate.cycleProperty(OPEN);
                worldIn.setBlockState(blockpos, state, 10);
                worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
                SoundType soundType = this.getSoundType(state, worldIn, pos, playerIn);
                worldIn.playSound(playerIn, pos, state.getValue(OPEN) ? this.getDoorOpenSound() : this.getDoorCloseSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.5F);
                return true;
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void toggleDoor(World worldIn, BlockPos pos, boolean open)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this)
        {
            BlockPos blockpos = iblockstate.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
            IBlockState blockState1 = pos == blockpos ? iblockstate : worldIn.getBlockState(blockpos);

            if (blockState1.getBlock() == this && blockState1.getValue(OPEN) != open)
            {
                worldIn.setBlockState(blockpos, blockState1.withProperty(OPEN, open), 10);
                worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
                SoundType soundType = this.getSoundType(this.getDefaultState(), worldIn, pos, null);
                worldIn.playSound(null, pos, open ? this.getDoorOpenSound() : this.getDoorCloseSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.5F);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_)
    {
        if (state.getValue(HALF) == EnumDoorHalf.UPPER)
        {
            BlockPos blockpos = pos.down();
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
            else if (blockIn != this)
            {
                iblockstate.neighborChanged(worldIn, blockpos, blockIn, p_189540_5_);
            }
        }
        else
        {
            boolean flag1 = false;
            BlockPos blockPos = pos.up();
            IBlockState blockState1 = worldIn.getBlockState(blockPos);

            if (blockState1.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }

            if (!worldIn.getBlockState(pos.down()).isOpaqueCube())
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;

                if (blockState1.getBlock() == this)
                {
                    worldIn.setBlockToAir(blockPos);
                }
            }

            if (flag1)
            {
                if (!worldIn.isRemote)
                {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                }
            }
            else
            {
                boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockPos);

                if (blockIn != this && (flag || blockIn.getDefaultState().canProvidePower()) && flag != blockState1.getValue(POWERED))
                {
                    worldIn.setBlockState(blockPos, blockState1.withProperty(POWERED, flag), 2);

                    if (flag != state.getValue(OPEN))
                    {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, flag), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        SoundType soundType = this.getSoundType(state, worldIn, pos, null);
                        worldIn.playSound(null, pos, flag ? this.getDoorOpenSound() : this.getDoorCloseSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.5F);
                    }
                }
            }
        }
    }
}
