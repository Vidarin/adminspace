package com.vidarin.adminspace.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockColumn extends BlockBase {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);

    public BlockColumn(String name) {
        this(name, Material.ROCK);
    }

    public BlockColumn(String name, Material material) {
        this(name, material, null);
    }

    public BlockColumn(String name, Material material, @Nullable CreativeTabs tab) {
        super(name, material, tab);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
            worldIn.setBlockState(pos, state.withProperty(AXIS, state.getValue(AXIS)), 2);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withRotation( IBlockState state, Rotation rot)
    {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.withProperty(AXIS, EnumFacing.Axis.Z);
                case Z -> state.withProperty(AXIS, EnumFacing.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing.Axis axis = EnumFacing.Axis.Y;
        int i = meta & 12;

        if (i == 4)
        {
            axis = EnumFacing.Axis.X;
        }
        else if (i == 8)
        {
            axis = EnumFacing.Axis.Z;
        }

        return this.getDefaultState().withProperty(AXIS, axis);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        EnumFacing.Axis enumfacing$axis = state.getValue(AXIS);

        if (enumfacing$axis == EnumFacing.Axis.X)
        {
            i |= 4;
        }
        else if (enumfacing$axis == EnumFacing.Axis.Z)
        {
            i |= 8;
        }

        return i;
    }

}
