package com.vidarin.adminspace.dimension.deltaquest;

import com.vidarin.adminspace.dimension.AdminspaceWorldProvider;
import com.vidarin.adminspace.dimension.deltaquest.generator.ChunkGeneratorDeltaQuest;
import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.MathUtil;
import com.vidarin.adminspace.render.sky.SkyRendererCustomTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
public class DimensionDeltaQuest extends AdminspaceWorldProvider {

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
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            float lightFactor = 1.0F - (float) i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - lightFactor) / (lightFactor * 5.0F + 1.0F);
            this.lightBrightnessTable[i] *= 0.4F + (0.2F * (float) i / 15.0F);
        }
    }

    private static final List<DimensionMusic> GENERIC = Arrays.asList(
            new DimensionMusic(SoundInit.ONE_CLIFFSIDE_HINSON, 1f, 1f, 7000),
            new DimensionMusic(SoundInit.ONE_DANNY_MAKES_CHIPTUNE, 1f, 1f, 4000),
            new DimensionMusic(SoundInit.ONE_DRUNKEN_CARBONI, 1f, 1f, 6000),
            new DimensionMusic(SoundInit.ONE_FAUX_VIDEO_PRODUCTION, 1f, 1f, 5000),
            new DimensionMusic(SoundInit.ONE_INDEPENDENT_ACCIDENT, 1f, 1f, 8000),
            new DimensionMusic(SoundInit.ONE_POST_SUCCESS_DEPRESSION, 1f, 1f, 6000),
            new DimensionMusic(SoundInit.ONE_THE_FIRST_MILLION, 1f, 1f, 8000),
            new DimensionMusic(SoundInit.ONE_THE_WEIRDEST_YEAR_OF_YOUR_LIFE, 1f, 1f, 7000)
    );
    private static final List<DimensionMusic> UNDERWATER = Arrays.asList(
            new DimensionMusic(SoundInit.ONE_BUILDUP_ERRORS, 1f, 1f, 6000),
            new DimensionMusic(SoundInit.ONE_CLUMSINESS_AND_INNOVATION, 1f, 1f, 5000),
            new DimensionMusic(SoundInit.ONE_SWARMS, 1f, 1f, 5000),
            new DimensionMusic(SoundInit.ONE_SURFACE_PENSION, 1f, 1f, 9000)
    );
    private static final List<DimensionMusic> HOME = Arrays.asList(
            new DimensionMusic(SoundInit.ONE_FOR_THE_SAKE_OF_MAKING_GAMES, 1f, 1f, 4000),
            new DimensionMusic(SoundInit.ONE_IMPOSTOR_SYNDROME, 1f, 1f, 5000),
            new DimensionMusic(SoundInit.ONE_LOST_COUSINS, 1f, 1f, 3000),
            new DimensionMusic(SoundInit.ONE_ONE_LAST_GAME, 1f, 1f, 4000),
            new DimensionMusic(SoundInit.ONE_PR_DEPARTMENT, 1f, 1f, 3000),
            new DimensionMusic(SoundInit.ONE_WOODEN_LOVE, 1f, 1f, 3000)
    );
    private static final List<DimensionMusic> CAVES = Arrays.asList(
            new DimensionMusic(SoundInit.ONE_JAYSON_GLOVE, 1f, 1f, 5000),
            new DimensionMusic(SoundInit.ONE_LAWYER_CAGE_FIGHT, 1f, 1f, 3000),
            new DimensionMusic(SoundInit.ONE_NO_PRESSURE, 1f, 1f, 6000),
            new DimensionMusic(SoundInit.ONE_PRELIMINARY_ART_FORM, 1f, 1f, 5000),
            new DimensionMusic(SoundInit.ONE_SOCIAL_LEGO, 1f, 1f, 7000),
            new DimensionMusic(SoundInit.ONE_TOTAL_DRAG, 1f, 1f, 4000)
    );

    @SuppressWarnings("ConstantValue")
    @Override
    public DimensionMusic playDimensionMusic(Random rand, EntityPlayer player) {
        if (player.isInWater()) return MathUtil.pick(UNDERWATER, rand);
        BlockPos spawn = player.getBedLocation(100);
        if (spawn != null && spawn.distanceSq(player.getPosition()) < 2500 && Math.abs(spawn.getY() - player.posY) <= 20) return MathUtil.pick(HOME, rand);
        else if (player.posY < world.getSeaLevel() && !player.world.canBlockSeeSky(player.getPosition())) return MathUtil.pick(CAVES, rand);
        else return MathUtil.pick(GENERIC, rand);
    }

    @Override
    public boolean isSkyColored() {
        return false;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public IRenderHandler getSkyRenderer() {
        return new SkyRendererCustomTexture(Adminspace.MODID, "deltaquest", true);
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
        Vec3d nightColor   = new Vec3d(0.09, 0.15, 0.3);
        Vec3d sunsetColor  = new Vec3d(1.0, 0.4, 0.3);
        Vec3d sunriseColor = new Vec3d(1.0, 0.6, 0.8);

        float fadeRange = 0.1F;

        if (celestialAngle < 0.25F - fadeRange || celestialAngle > 0.75F + fadeRange) {
            return dayColor;
        }

        if (celestialAngle >= 0.20F && celestialAngle <= 0.30F) {
            float blend = (celestialAngle - 0.20F) / 0.10F;
            return MathUtil.blend(sunsetColor, nightColor, blend);
        }

        if (celestialAngle >= 0.70F && celestialAngle <= 0.80F) {
            float blend = (celestialAngle - 0.70F) / 0.10F;
            return MathUtil.blend(nightColor, sunriseColor, blend);
        }

        if (celestialAngle >= 0.15F && celestialAngle < 0.20F) {
            float blend = (celestialAngle - 0.15F) / 0.05F;
            return MathUtil.blend(dayColor, sunsetColor, blend);
        }

        if (celestialAngle > 0.80F) {
            float blend = (celestialAngle - 0.80F) / 0.05F;
            return MathUtil.blend(sunriseColor, dayColor, blend);
        }

        return nightColor;
    }
}
