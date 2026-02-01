package com.vidarin.adminspace.mixin;

import com.vidarin.adminspace.data.AdminspacePlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Framebuffer.class)
public class MixinFramebuffer {
    @Inject(method = "framebufferRenderExt(IIZ)V", at = @At("HEAD"), cancellable = true)
    public void adminspace$framebufferRenderExt(int width, int height, boolean idk, CallbackInfo ci) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (player != null && !Minecraft.getMinecraft().isGamePaused() && AdminspacePlayerData.getData(player).getBlindedDuration() > 0) {
            GlStateManager.disableTexture2D();
            GlStateManager.clearColor(0F, 0F, 0F, 1F);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GlStateManager.enableTexture2D();
            ci.cancel();
        }
    }
}
