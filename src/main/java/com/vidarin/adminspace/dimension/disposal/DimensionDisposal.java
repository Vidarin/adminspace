package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.dimension.AdminspaceWorldProvider;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import mcp.MethodsReturnNonnullByDefault;

import java.util.*;

@MethodsReturnNonnullByDefault
public class DimensionDisposal extends AdminspaceWorldProvider {
    public DimensionDisposal() {
        this.biomeProvider = new BiomeProviderSingle(BiomeInit.DISPOSAL_DIM);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionInit.DISPOSAL;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorDisposal(this.world, this.getSeed());
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();
        for (EntityPlayerMP player : players) {
            player.sendStatusMessage(new TextComponentString("This dimension is not finished."), true);
        }
    }

    @Override
    public DimensionMusic playDimensionMusic(Random rand, EntityPlayer player) {
        return new DimensionMusic(SoundInit.DISPOSAL_MUSIC, 1F, 1F, 1200);
    }
}
