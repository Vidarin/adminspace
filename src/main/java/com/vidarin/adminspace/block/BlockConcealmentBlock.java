package com.vidarin.adminspace.block;

import com.vidarin.adminspace.item.ItemDismantler;
import com.vidarin.adminspace.util.Fonts;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class BlockConcealmentBlock extends BlockBase {
    public BlockConcealmentBlock() {
        super("concealment_block");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        super.onBlockClicked(worldIn, pos, playerIn);
        if (!playerIn.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            if (playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemDismantler) {
                if (playerIn.getHeldItem(EnumHand.MAIN_HAND).hasTagCompound()) {
                    if (Objects.requireNonNull(playerIn.getHeldItem(EnumHand.MAIN_HAND).getTagCompound()).getBoolean("Activated") && Objects.requireNonNull(playerIn.getHeldItem(EnumHand.MAIN_HAND).getTagCompound()).getString("Owner").equals(playerIn.getName())) {
                        worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1.5F, true);
                        playerIn.getHeldItem(EnumHand.MAIN_HAND).damageItem(800, playerIn);
                        worldIn.setBlockToAir(pos);
                        if (worldIn.isRemote) {
                            playerIn.sendMessage(new TextComponentString(Fonts.Red + "The block exploded and severely damaged your dismantler!"));
                            playerIn.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 2.5F, 1.0F);
                            playerIn.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                        }
                        if (!worldIn.isRemote) {
                            EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
                            playerMP.connection.sendPacket(new SPacketBlockChange(worldIn, pos));
                        }
                    }
                }
            }
        }
    }
}
