package com.vidarin.adminspace.main;

import com.vidarin.adminspace.block.tileentity.TileEntityVoidChest;
import com.vidarin.adminspace.entity.EntityIntegrity;
import com.vidarin.adminspace.render.entity.RenderIntegrity;
import com.vidarin.adminspace.render.entity.RenderVoidChest;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RegisterRenderers {
    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityIntegrity.class, RenderIntegrity::new);
    }

    public static void registerTileEntityRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVoidChest.class, new RenderVoidChest());
    }
}
