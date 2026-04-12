package com.vidarin.adminspace.render.entity;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.render.model.ModelVoidChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderVoidChest extends TileEntitySpecialRenderer<TileEntityVoidChest> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Adminspace.MODID + ":textures/entity/void_chest.png");

    private final ModelVoidChest MODEL = new ModelVoidChest();

    @Override
    public void render(@NotNull TileEntityVoidChest chest, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(true);
        int meta = 0;

        if (chest.hasWorld()) meta = chest.getBlockMetadata();

        ModelVoidChest model = MODEL;
        this.bindTexture(TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int rotationDegrees = switch (meta) {
            case 2 -> 180;
            case 4 -> 90;
            case 5 -> -90;
            default -> 0;
        };

        GlStateManager.rotate((float) rotationDegrees, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float lidAngle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        model.chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
        model.chestKnob.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
        model.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
