package com.vidarin.adminspace.dimension.deltaquest;

import com.vidarin.adminspace.dimension.deltaquest.generator.ChunkGeneratorDeltaQuest;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

@MethodsReturnNonnullByDefault
public class DimensionDeltaQuest extends WorldProvider {

    public DimensionDeltaQuest() {
        this.biomeProvider = new BiomeProviderSingle(BiomeInit.DELTAQUEST_FOREST);
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorDeltaQuest(this.world, this.getSeed());
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionInit.DELTAQUEST;
    }

    @Override
    public boolean canDropChunk(int x, int z) {
        return !world.isSpawnChunk(x, z);
    }

    @Override
    public boolean doesXZShowFog(int x, int z) {
        return true;
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();
        if (world.isRemote) {
            try {
                Minecraft.getMinecraft().gameSettings.gammaSetting = 0.0F;
            }
            catch (Throwable ignored) {}
        }
    }
}
