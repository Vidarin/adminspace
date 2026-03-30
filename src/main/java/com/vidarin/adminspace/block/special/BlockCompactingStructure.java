package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.BlockBase;
import com.vidarin.adminspace.block.tileentity.SyncedTileEntity;
import com.vidarin.adminspace.data.WriteOnlyCompactStructureData;
import com.vidarin.adminspace.util.Fonts;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockCompactingStructure extends BlockBase implements ITileEntityProvider {
    public BlockCompactingStructure() {
        super("compacting_structure_block");
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCompactingStructureBlock();
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntityCompactingStructureBlock te = (TileEntityCompactingStructureBlock) worldIn.getTileEntity(pos);
            if (te == null) throw new NullPointerException("null");
            else {
                if (hitX > 0.8) {
                    if (playerIn.isSneaking()) te.xOff++;
                    else te.xSize++;
                }
                if (hitX < 0.2) {
                    if (playerIn.isSneaking()) te.xOff--;
                    else te.xSize--;
                }
                if (hitY > 0.8) {
                    if (playerIn.isSneaking()) te.yOff++;
                    else te.ySize++;
                }
                if (hitY < 0.2) {
                    if (playerIn.isSneaking()) te.yOff--;
                    else te.ySize--;
                }
                if (hitZ > 0.8) {
                    if (playerIn.isSneaking()) te.zOff++;
                    else te.zSize++;
                }
                if (hitZ < 0.2) {
                    if (playerIn.isSneaking()) te.zOff--;
                    else te.zSize--;
                }
                te.markDirty();
            }
        }
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            TileEntityCompactingStructureBlock te = (TileEntityCompactingStructureBlock) worldIn.getTileEntity(pos);
            if (te == null) throw new NullPointerException("null");
            if (playerIn.isSneaking()) {
                WriteOnlyCompactStructureData.init();
                te.name = playerIn.getHeldItemMainhand().getDisplayName(); // I really don't want to make a gui ok
                te.markDirty();
                te.save();
                playerIn.sendMessage(new TextComponentString(Fonts.Green + "Saved as " + Fonts.Reset + te.name + Fonts.Green + "!"));
            } else {
                playerIn.sendMessage(new TextComponentString("Size: " + Fonts.Red + te.xSize + " " + Fonts.Green + te.ySize + " " + Fonts.Blue + te.zSize
                        + Fonts.Reset + " Offset: " + Fonts.Red + te.xOff + " " + Fonts.Green + te.yOff + " " + Fonts.Blue + te.zOff));
            }
        }
    }

    public static class TileEntityCompactingStructureBlock extends SyncedTileEntity implements ITickable {
        public int xOff = 0, yOff = 0, zOff = 0;
        public int xSize = 0, ySize = 0, zSize = 0;
        public String name = "";

        @Override
        public void update() {
            if (world.getTotalWorldTime() % 5 == 0) {
                    world.spawnParticle(EnumParticleTypes.REDSTONE,       pos.getX() + xOff + xSize, pos.getY() + yOff, pos.getZ() + zOff, 0, 0, 0);
                    world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + xOff, pos.getY() + yOff + ySize, pos.getZ() + zOff, 0, 0, 0);
                    world.spawnParticle(EnumParticleTypes.WATER_SPLASH,   pos.getX() + xOff, pos.getY() + yOff, pos.getZ() + zOff + zSize, 0, 0, 0);
            }
        }

        public void setValues(int xOff, int yOff, int zOff, int xSize, int ySize, int zSize, String name) {
            this.xOff = xOff;
            this.yOff = yOff;
            this.zOff = zOff;
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
            this.name = name;
        }

        public void save() {
            WriteOnlyCompactStructureData csd = new WriteOnlyCompactStructureData(
                    this.name,
                    new Vec3i(pos.getX() + xOff, pos.getY() + yOff, pos.getZ() + zOff),
                    new Vec3i(pos.getX() + xOff + xSize, pos.getY() + yOff + ySize, pos.getZ() + zOff + zSize),
                    this.world
            );
            csd.write();
        }

        @Override
        public void readNBT(NBTTagCompound compound) {
            this.xOff = compound.getInteger("xOff");
            this.yOff = compound.getInteger("yOff");
            this.zOff = compound.getInteger("zOff");
            this.xSize = compound.getInteger("xSize");
            this.ySize = compound.getInteger("ySize");
            this.zSize = compound.getInteger("zSize");
            this.name = compound.getString("name");
        }

        @Override
        public NBTTagCompound writeNBT(NBTTagCompound compound) {
            compound.setInteger("xOff", this.xOff);
            compound.setInteger("yOff", this.yOff);
            compound.setInteger("zOff", this.zOff);
            compound.setInteger("xSize", this.xSize);
            compound.setInteger("ySize", this.ySize);
            compound.setInteger("zSize", this.zSize);
            compound.setString("name", this.name);
            return compound;
        }
    }
}
