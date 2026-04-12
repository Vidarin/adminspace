package com.vidarin.adminspace.mixin;

import com.vidarin.adminspace.init.DimensionInit;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow private static Minecraft instance;

    @Inject(method = "isAmbientOcclusionEnabled", at = @At("HEAD"), cancellable = true)
    private static void adminspace$isAmbientOcclusionEnabled(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(instance != null && instance.gameSettings.ambientOcclusion != 0 && instance.player.dimension != DimensionInit.DELTAQUEST.getId());
    }
}
