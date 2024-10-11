package com.vidarin.adminspace.main;

import com.vidarin.adminspace.entity.EntityIntegrity;
import com.vidarin.adminspace.model.render.RenderIntegrity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RegisterRenderers {
    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityIntegrity.class, RenderIntegrity::new);
    }
}
