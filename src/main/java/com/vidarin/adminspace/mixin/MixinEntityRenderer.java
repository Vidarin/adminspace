package com.vidarin.adminspace.mixin;

import com.vidarin.adminspace.init.DimensionInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method = "updateLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F", opcode = Opcodes.GETFIELD))
    public float adminspace$gammaSetting$updateLightmap(GameSettings instance) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.player.dimension == DimensionInit.DELTAQUEST.getId()) return 0.0f;
        return instance.gammaSetting;
    }
}
