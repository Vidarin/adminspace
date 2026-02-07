package com.vidarin.adminspace.block.special;

import com.vidarin.adminspace.block.BlockBase;
import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockTerminal extends BlockBase implements ITileEntityProvider {
    public static final int PERM_LEVEL_NORMAL = 1;
    public static final int PERM_LEVEL_MAIN = 2;
    public static final int PERM_LEVEL_ADMINSPACE = 3;
    public static final int PERM_LEVEL_AUTHOR = 4;

    private final int permLevel;

    public BlockTerminal(String name, int permLevel) {
        super(name, Material.IRON);
        this.permLevel = permLevel;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        TileEntityTerminal terminal = new TileEntityTerminal();
        terminal.setPermLevel(permLevel);

        return terminal;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) Adminspace.openGui(player, world, pos);
        return true;
    }
}