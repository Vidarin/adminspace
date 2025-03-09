package com.vidarin.adminspace.dimension.deltaquest;

import com.vidarin.adminspace.dimension.deltaquest.generator.ChunkGeneratorDeltaQuest;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.skyrenderer.SkyRendererCustomTexture;
import mods.thecomputerizer.theimpossiblelibrary.util.file.DataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

@MethodsReturnNonnullByDefault
public class DimensionDeltaQuest extends WorldProvider {
    private boolean hasSavedSettings = false;

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
    public void onPlayerAdded(@Nonnull EntityPlayerMP player) {
        super.onPlayerAdded(player);
        this.getPlayerSettings(player);
        this.hasSavedSettings = true;
    }

    @Override
    public void onPlayerRemoved(@Nonnull EntityPlayerMP player) {
        super.onPlayerRemoved(player);
        this.resetPlayerSettings(player);
        this.hasSavedSettings = false;
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();
        if (this.hasSavedSettings) this.updatePlayerSettings();
    }

    @SideOnly(Side.CLIENT)
    private void getPlayerSettings(EntityPlayer player) {
        try {
            int currentSetting = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(player.getUniqueID() + "_ambientOcclusion", currentSetting);
            try {
                DataUtil.writeGlobalData(compound, Adminspace.MODID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Throwable throwable) {
            System.err.println("Data saving failed!");
            throwable.printStackTrace(System.err);
        }
    }

    @SideOnly(Side.CLIENT)
    private void resetPlayerSettings(EntityPlayer player) {
        try {
            Minecraft.getMinecraft().gameSettings.ambientOcclusion = DataUtil.getGlobalData(Adminspace.MODID, false).getInteger(player.getUniqueID() + "_ambientOcclusion");
        } catch (Throwable throwable) {
            System.err.println("Data retrieving failed!");
            throwable.printStackTrace(System.err);
        }
    }

    @SideOnly(Side.CLIENT)
    private void updatePlayerSettings() {
        Minecraft.getMinecraft().gameSettings.ambientOcclusion = 0;
    }

    @Override
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            float lightFactor = 1.0F - (float) i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - lightFactor) / (lightFactor * 5.0F + 1.0F);
            this.lightBrightnessTable[i] *= 0.6F + (0.4F * (float) i / 15.0F);
        }
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public IRenderHandler getSkyRenderer() {
        return new SkyRendererCustomTexture("deltaquest");
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Nullable
    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return null;
    }
}
