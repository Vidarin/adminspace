package com.vidarin.adminspace.block;

import com.vidarin.adminspace.block.tileentity.TileEntityTerminal;
import com.vidarin.adminspace.gui.GuiIDs;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class BlockTerminal extends BlockBase implements ITileEntityProvider {
    private final String name;

    public BlockTerminal(String name) {
        super(name, Material.IRON);
        this.name = name;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        TileEntityTerminal tileEntityTerminal = new TileEntityTerminal();
        tileEntityTerminal.setPermLevel(this.getPermLevel());

        return tileEntityTerminal;
    }

    private int getPermLevel() {
        int permLevel = 0;
        if (Objects.equals(name, "terminal")) permLevel = 1;
        else if (Objects.equals(name, "main_terminal")) permLevel = 2;
        else if (Objects.equals(name, "adminspace_terminal")) permLevel = 3;
        else if (Objects.equals(name, "authors_terminal")) permLevel = 4;
        return permLevel;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote)
            player.openGui(Adminspace.INSTANCE, GuiIDs.GUI_TERMINAL, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

}