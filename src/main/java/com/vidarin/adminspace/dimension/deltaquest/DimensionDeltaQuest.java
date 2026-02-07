package com.vidarin.adminspace.dimension.deltaquest;

import com.vidarin.adminspace.data.AdminspaceWorldData;
import com.vidarin.adminspace.dimension.deltaquest.generator.ChunkGeneratorDeltaQuest;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.SPacketUpdateVariablesMap;
import com.vidarin.adminspace.util.MathUtil;
import com.vidarin.adminspace.util.skyrenderer.SkyRendererCustomTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
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
        int prevAmbientOcclusion = Minecraft.getMinecraft().gameSettings.ambientOcclusion;
        AdminspaceNetworkHandler.INSTANCE.sendToServer(new SPacketUpdateVariablesMap("ambientOcclusion", player.getUniqueID().toString(), prevAmbientOcclusion));
    }

    @SideOnly(Side.CLIENT)
    private void resetPlayerSettings(EntityPlayer player) {
        Minecraft.getMinecraft().gameSettings.ambientOcclusion =
                AdminspaceWorldData.get(player.world).getAmbientOcclusionValue(player.getUniqueID());
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
            this.lightBrightnessTable[i] *= 0.4F + (0.2F * (float) i / 15.0F);
        }
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public IRenderHandler getSkyRenderer() {
        return new SkyRendererCustomTexture("deltaquest", true);
    }

    @Nullable
    @Override
    public IRenderHandler getCloudRenderer() {
        return new IRenderHandler() {
            @Override
            public void render(float partialTicks, WorldClient world, Minecraft mc) {}
        };
    }

    @Nullable
    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return null;
    }

    @Override
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        Vec3d dayColor     = new Vec3d(0.65, 0.79, 1.0);
        Vec3d nightColor   = new Vec3d(0.03, 0.05, 0.1);
        Vec3d sunsetColor  = new Vec3d(1.0, 0.4, 0.3);
        Vec3d sunriseColor = new Vec3d(1.0, 0.6, 0.8);

        float fadeRange = 0.1F;

        if (celestialAngle < 0.25F - fadeRange || celestialAngle > 0.75F + fadeRange) {
            return dayColor;
        }

        if (celestialAngle >= 0.20F && celestialAngle <= 0.30F) {
            float blend = (celestialAngle - 0.20F) / 0.10F;
            return MathUtil.lerp(sunsetColor, nightColor, blend);
        }

        if (celestialAngle >= 0.70F && celestialAngle <= 0.80F) {
            float blend = (celestialAngle - 0.70F) / 0.10F;
            return MathUtil.lerp(nightColor, sunriseColor, blend);
        }

        if (celestialAngle >= 0.15F && celestialAngle < 0.20F) {
            float blend = (celestialAngle - 0.15F) / 0.05F;
            return MathUtil.lerp(dayColor, sunsetColor, blend);
        }

        if (celestialAngle > 0.80F) {
            float blend = (celestialAngle - 0.80F) / 0.05F;
            return MathUtil.lerp(sunriseColor, dayColor, blend);
        }

        return nightColor;
    }
}
