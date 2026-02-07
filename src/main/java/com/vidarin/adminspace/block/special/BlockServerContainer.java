package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.tileentity.TileEntityServerContainer;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockServerContainer extends BlockContainer {
    public BlockServerContainer() {
        super(Material.ROCK);
        this.setTranslationKey("server_container");
        this.setRegistryName("server_container");
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(SoundType.METAL);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName("server_container"));
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileEntityServerContainer(worldIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            Adminspace.openGui(playerIn, worldIn, pos);
        }

        return true;
    }
}
