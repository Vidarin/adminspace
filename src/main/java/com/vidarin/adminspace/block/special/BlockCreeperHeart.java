package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.BlockBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class BlockCreeperHeart extends BlockBase {
    protected static final AxisAlignedBB CREEPER_HEART_AABB = new AxisAlignedBB(0.125F, 0.125F, 0.125F, 0.875F, 0.875F, 0.875F);

    public BlockCreeperHeart() {
        super("creeper_heart", Material.CLAY, CreativeTabs.MISC);
        this.setHardness(2.0F);
        this.setResistance(1.0F);
        this.setSoundType(SoundType.SLIME);
    }

    @Override
    public @NotNull BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public @NotNull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return CREEPER_HEART_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 0.5F, false);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(world, pos, explosion);
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1.5F, true);
    }
}
