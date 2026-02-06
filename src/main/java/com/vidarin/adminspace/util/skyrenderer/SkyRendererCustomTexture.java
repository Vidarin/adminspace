package com.vidarin.adminspace.util.skyrenderer;

import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

public class SkyRendererCustomTexture extends IRenderHandler {
    private final ResourceLocation SKYBOX_TEXTURE_N;
    private final ResourceLocation SKYBOX_TEXTURE_S;
    private final ResourceLocation SKYBOX_TEXTURE_W;
    private final ResourceLocation SKYBOX_TEXTURE_E;
    private final ResourceLocation SKYBOX_TEXTURE_U;
    private final ResourceLocation SKYBOX_TEXTURE_D;
    private final ResourceLocation SUN_TEXTURE;
    private final ResourceLocation MOON_TEXTURE;

    private final boolean shouldRenderCelestialObjects;
    private final boolean shouldTintSkybox;

    public SkyRendererCustomTexture(String skyboxFolder, boolean tintSkybox) {
        this.shouldRenderCelestialObjects = false;
        this.shouldTintSkybox = tintSkybox;

        this.SKYBOX_TEXTURE_N = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_n.png");
        this.SKYBOX_TEXTURE_S = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_s.png");
        this.SKYBOX_TEXTURE_E = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_e.png");
        this.SKYBOX_TEXTURE_W = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_w.png");
        this.SKYBOX_TEXTURE_U = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_u.png");
        this.SKYBOX_TEXTURE_D = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_d.png");

        this.SUN_TEXTURE = null;
        this.MOON_TEXTURE = null;
    }

    public SkyRendererCustomTexture(String skyboxFolder, ResourceLocation sun, ResourceLocation moon, boolean tintSkybox) {
        this.shouldRenderCelestialObjects = true;
        this.shouldTintSkybox = tintSkybox;

        this.SKYBOX_TEXTURE_N = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_n.png");
        this.SKYBOX_TEXTURE_S = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_s.png");
        this.SKYBOX_TEXTURE_E = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_e.png");
        this.SKYBOX_TEXTURE_W = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_w.png");
        this.SKYBOX_TEXTURE_U = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_u.png");
        this.SKYBOX_TEXTURE_D = new ResourceLocation(Adminspace.MODID, "textures/sky/" + skyboxFolder + "/sky_d.png");

        this.SUN_TEXTURE = sun;
        this.MOON_TEXTURE = moon;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderSkybox(world, mc, partialTicks);

        if (shouldRenderCelestialObjects)
            renderCelestialObjects(world, mc, partialTicks);

        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
    }

    private void renderSkybox(WorldClient world, Minecraft mc, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();

        float celestialAngle = world.getCelestialAngle(partialTicks) * 360.0F;
        GlStateManager.rotate(celestialAngle, 1.0F, 0.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float size = mc.gameSettings.renderDistanceChunks * 18;

        float tintStrength = 0.75f;
        Vec3d color = world.getFogColor(partialTicks);

        float r = (float)(color.x * tintStrength + (1.0 - tintStrength));
        float g = (float)(color.y * tintStrength + (1.0 - tintStrength));
        float b = (float)(color.z * tintStrength + (1.0 - tintStrength));

        for (int face = 0; face < 6; ++face) {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            switch (face) {
                case 0: // North
                    if (this.shouldTintSkybox) GlStateManager.color(r, g, b, tintStrength);
                    mc.renderEngine.bindTexture(SKYBOX_TEXTURE_N);
                    buffer.pos(-size, -size, -size).tex(0, 0).endVertex();
                    buffer.pos(size, -size, -size).tex(1, 0).endVertex();
                    buffer.pos(size, size, -size).tex(1, 1).endVertex();
                    buffer.pos(-size, size, -size).tex(0, 1).endVertex();
                    break;
                case 1: // South
                    if (this.shouldTintSkybox) GlStateManager.color(r, g, b, tintStrength);
                    mc.renderEngine.bindTexture(SKYBOX_TEXTURE_S);
                    buffer.pos(size, -size, size).tex(0, 0).endVertex();
                    buffer.pos(-size, -size, size).tex(1, 0).endVertex();
                    buffer.pos(-size, size, size).tex(1, 1).endVertex();
                    buffer.pos(size, size, size).tex(0, 1).endVertex();
                    break;
                case 2: // East
                    if (this.shouldTintSkybox) GlStateManager.color(r, g, b, tintStrength);
                    mc.renderEngine.bindTexture(SKYBOX_TEXTURE_E);
                    buffer.pos(size, -size, -size).tex(0, 0).endVertex();
                    buffer.pos(size, -size, size).tex(1, 0).endVertex();
                    buffer.pos(size, size, size).tex(1, 1).endVertex();
                    buffer.pos(size, size, -size).tex(0, 1).endVertex();
                    break;
                case 3: // West
                    if (this.shouldTintSkybox) GlStateManager.color(r, g, b, tintStrength);
                    mc.renderEngine.bindTexture(SKYBOX_TEXTURE_W);
                    buffer.pos(-size, -size, size).tex(0, 0).endVertex();
                    buffer.pos(-size, -size, -size).tex(1, 0).endVertex();
                    buffer.pos(-size, size, -size).tex(1, 1).endVertex();
                    buffer.pos(-size, size, size).tex(0, 1).endVertex();
                    break;
                case 4: // Up
                    if (this.shouldTintSkybox) GlStateManager.color(r, g, b, tintStrength);
                    mc.renderEngine.bindTexture(SKYBOX_TEXTURE_U);
                    buffer.pos(-size, size, -size).tex(0, 0).endVertex();
                    buffer.pos(size, size, -size).tex(1, 0).endVertex();
                    buffer.pos(size, size, size).tex(1, 1).endVertex();
                    buffer.pos(-size, size, size).tex(0, 1).endVertex();
                    break;
                case 5: // Down
                    if (this.shouldTintSkybox) GlStateManager.color(r, g, b, tintStrength);
                    mc.renderEngine.bindTexture(SKYBOX_TEXTURE_D);
                    buffer.pos(-size, -size, size).tex(0, 0).endVertex();
                    buffer.pos(size, -size, size).tex(1, 0).endVertex();
                    buffer.pos(size, -size, -size).tex(1, 1).endVertex();
                    buffer.pos(-size, -size, -size).tex(0, 1).endVertex();
                    break;
            }

            tessellator.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderCelestialObjects(WorldClient world, Minecraft mc, float partialTicks) {
        float celestialAngle = world.getCelestialAngle(partialTicks);
        if (SUN_TEXTURE == null || MOON_TEXTURE == null) return;

        // Render Sun
        GlStateManager.pushMatrix();
        mc.renderEngine.bindTexture(SUN_TEXTURE);
        GlStateManager.rotate(celestialAngle * 360.0F, 1.0F, 0.0F, 0.0F);
        renderQuad(30.0F);
        GlStateManager.popMatrix();

        // Render Moon
        GlStateManager.pushMatrix();
        mc.renderEngine.bindTexture(MOON_TEXTURE);
        GlStateManager.rotate(celestialAngle * 360.0F + 180.0F, 1.0F, 0.0F, 0.0F);
        renderQuad(20.0F);
        GlStateManager.popMatrix();
    }

    private void renderQuad(float size) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float halfSize = size / 2.0F;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-halfSize, -halfSize, 0.0D).tex(0.0D, 0.0D).endVertex();
        buffer.pos(halfSize, -halfSize, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(halfSize, halfSize, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(-halfSize, halfSize, 0.0D).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }
}
