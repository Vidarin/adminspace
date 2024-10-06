package com.vidarin.adminspace.models.render;

import com.vidarin.adminspace.entity.EntityIntegrity;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.models.ModelEntityIntegrity;
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
    protected void applyRotations(@Nonnull EntityIntegrity entity, float rotationPitch, float rotationYaw, float partialTicks) {
        super.applyRotations(entity, rotationPitch, rotationYaw, partialTicks);
    }
}
