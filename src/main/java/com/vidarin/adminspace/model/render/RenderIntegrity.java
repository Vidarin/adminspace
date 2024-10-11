package com.vidarin.adminspace.model.render;

import com.vidarin.adminspace.entity.EntityIntegrity;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.model.ModelEntityIntegrity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderIntegrity extends RenderLiving<EntityIntegrity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Adminspace.MODID + ":textures/entity/integrity.png");

    public RenderIntegrity(RenderManager manager) {
        super(manager, new ModelEntityIntegrity(), 0.5f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityIntegrity entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(@Nonnull EntityIntegrity entity, float partialTicks) {
        super.preRenderCallback(entity, partialTicks);

        EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();
        assert player != null;
        Vec3d viewerDirection = player.getLookVec();

        double rotationYaw = Math.atan2(viewerDirection.z, viewerDirection.x) * 180 / Math.PI;

        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) rotationYaw + 35.0f, 0, 1, 0);
    }

    @Override
    public void doRender(@Nonnull EntityIntegrity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.popMatrix();
    }
}