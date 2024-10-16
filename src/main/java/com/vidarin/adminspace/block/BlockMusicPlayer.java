package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
public class BlockMusicPlayer extends BlockJukebox {
    public BlockMusicPlayer() {
        super();
        this.setUnlocalizedName("music_player");
        this.setRegistryName("music_player");
        this.setHardness(-1.0f);
        this.setResistance(999999.9f);
        this.setSoundType(SoundType.METAL);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(HAS_RECORD))
        {
            this.dropRecord(worldIn, pos);
            state = state.withProperty(HAS_RECORD, Boolean.FALSE);
            worldIn.setBlockState(pos, state, 2);
            return true;
        }
        else if (playerIn.getHeldItem(hand).getItem() instanceof ItemRecord) {
            IBlockState blockState = worldIn.getBlockState(pos);

            ItemStack itemStack = playerIn.getHeldItem(hand);
            ((BlockJukebox)BlockInit.musicPlayer).insertRecord(worldIn, pos, blockState, itemStack);
            worldIn.playEvent(null, 1010, pos, Item.getIdFromItem(itemStack.getItem()));
            itemStack.shrink(1);
            playerIn.addStat(StatList.RECORD_PLAYED);

            state = state.withProperty(HAS_RECORD, Boolean.TRUE);
            worldIn.setBlockState(pos, state, 2);
            return true;
        }
        else return false;
    }

    private void dropRecord(World worldIn, BlockPos pos)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox)
            {
                BlockJukebox.TileEntityJukebox tileEntityJukebox = (BlockJukebox.TileEntityJukebox)tileentity;
                ItemStack itemStack = tileEntityJukebox.getRecord();

                if (!itemStack.isEmpty())
                {
                    worldIn.playEvent(1010, pos, 0);
                    worldIn.playRecord(pos, null);
                    tileEntityJukebox.setRecord(ItemStack.EMPTY);
                    double d0 = (double)(worldIn.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
                    double d1 = (double)(worldIn.rand.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                    double d2 = (double)(worldIn.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
                    ItemStack itemStack1 = itemStack.copy();
                    EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemStack1);
                    entityitem.setDefaultPickupDelay();
                    worldIn.spawnEntity(entityitem);
                }
            }
        }
    }
}
