package com.vidarin.adminspace.block;

import com.vidarin.adminspace.block.tileentity.TileEntityKeySlotter;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.inventory.GuiIDs;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockKeySlotter extends BlockContainer {
    public BlockKeySlotter() {
        super(Material.ROCK);
        this.setTranslationKey("key_slotter");
        this.setRegistryName("key_slotter");
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(SoundType.METAL);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName("key_slotter"));
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityKeySlotter(worldIn);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(hand);
            TileEntityKeySlotter te = (TileEntityKeySlotter) worldIn.getTileEntity(pos);
            if (te == null) return false;

            if (!te.hasKey()) {
                if (stack.getItem() == ItemInit.voidKey) { stack.shrink(1); te.setHasKey(true); }
                else playerIn.openGui(Adminspace.INSTANCE, GuiIDs.GUI_KEY_SLOTTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
            } else {
                if (stack.isEmpty()) { playerIn.setHeldItem(hand, new ItemStack(ItemInit.voidKey)); te.setHasKey(false); }
                else if (stack.getItem() == ItemInit.voidKey) { stack.grow(1); te.setHasKey(false); }
                else playerIn.openGui(Adminspace.INSTANCE, GuiIDs.GUI_KEY_SLOTTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
