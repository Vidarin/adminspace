package com.vidarin.adminspace.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTicker.class)
public class MixinMusicTicker {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void adminspace$update(CallbackInfo ci) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.provider != null)
            if (Minecraft.getMinecraft().world.provider.getMusicType() == null)
                ci.cancel();
    }
}
