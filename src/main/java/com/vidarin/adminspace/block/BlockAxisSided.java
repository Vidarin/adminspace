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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockAxisSided extends BlockBase {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    public BlockAxisSided(String name) {
        this(name, Material.ROCK);
    }

    public BlockAxisSided(String name, Material material) {
        this(name, material, null);
    }

    public BlockAxisSided(String name, Material material, CreativeTabs tab) {
        super(name, material, tab);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
            worldIn.setBlockState(pos, state.withProperty(AXIS, state.getValue(AXIS)), 2);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nonnull IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public @Nonnull IBlockState withRotation(@Nonnull IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS))
                {
                    case X:
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);

                    case Z:
                        return state.withProperty(AXIS, EnumFacing.Axis.X);

                    default:
                        return state;
                }

            default:
                return state;
        }
    }

    @Override
    protected @Nonnull BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS);
    }

    public @Nonnull IBlockState getStateFromMeta(int meta)
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
