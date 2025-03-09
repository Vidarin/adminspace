package com.vidarin.adminspace.model.render;

import com.vidarin.adminspace.entity.EntityIntegrity;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.model.ModelEntityIntegrity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

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
    public void doRender(@Nonnull EntityIntegrity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y + 1.5F, z);

        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 0.5F, 0.0f);
        GlStateManager.rotate(this.renderManager.options.thirdPersonView == 2 ? -this.renderManager.playerViewX : this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        this.bindEntityTexture(entity);
        this.mainModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }
}