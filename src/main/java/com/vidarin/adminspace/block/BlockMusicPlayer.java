package com.vidarin.adminspace.block;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.init.ItemInit;
import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.item.ItemSpecialRecord;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlockMusicPlayer extends BlockJukebox {
    private final Map<BlockPos, ISound> mapSoundPositions = new HashMap<>();

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
            if (!worldIn.isRemote) {
                worldIn.playEvent(null, 1010, pos, Item.getIdFromItem(itemStack.getItem()));
                itemStack.shrink(1);
                playerIn.addStat(StatList.RECORD_PLAYED);
            }

            state = state.withProperty(HAS_RECORD, Boolean.TRUE);
            worldIn.setBlockState(pos, state, 2);
            return true;
        }
        else if (playerIn.getHeldItem(hand).getItem() instanceof ItemSpecialRecord) {
            ItemStack itemStack = playerIn.getHeldItem(hand);

            ((BlockJukebox.TileEntityJukebox) Objects.requireNonNull(worldIn.getTileEntity(pos))).setRecord(itemStack.copy());
            if (!worldIn.isRemote) {
                switch (((ItemSpecialRecord) playerIn.getHeldItem(hand).getItem()).getUntranslatedRecordName()) {
                    case "calm_5":
                        this.playSpecialRecord(SoundInit.RECORD_CALM_5, pos, "? - Calm 5");
                }
                itemStack.shrink(1);
                playerIn.addStat(StatList.RECORD_PLAYED);
            }

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
                    this.playSpecialRecord(null, pos, null);
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

    @SideOnly(Side.CLIENT)
    private void playSpecialRecord(@Nullable SoundEvent soundIn, BlockPos pos, @Nullable String trackName) {
        ISound sound = this.mapSoundPositions.get(pos);

        if (sound != null)
        {
            Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
            this.mapSoundPositions.remove(pos);
        }

        if (soundIn != null)
        {
            if (trackName != null)
                Minecraft.getMinecraft().ingameGUI.setRecordPlayingMessage(trackName);

            ISound positionedSoundRecord = PositionedSoundRecord.getRecordSoundRecord(soundIn, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            this.mapSoundPositions.put(pos, positionedSoundRecord);
            if (Minecraft.getMinecraft().world.provider.getMusicType() != null)
                Minecraft.getMinecraft().getSoundHandler().stopSound(PositionedSoundRecord.getMusicRecord(Minecraft.getMinecraft().world.provider.getMusicType().getMusicLocation()));
            Minecraft.getMinecraft().getSoundHandler().playSound(positionedSoundRecord);
        }
    }
}
