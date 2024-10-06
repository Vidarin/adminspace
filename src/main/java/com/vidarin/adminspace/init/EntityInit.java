package com.vidarin.adminspace.init;

import com.vidarin.adminspace.entity.EntityIntegrity;
import com.vidarin.adminspace.entity.EntityNums;
import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit {
    public static void registerEntities() {
        registerEntity("integrity", EntityIntegrity.class, EntityNums.ENTITY_INTEGRITY, 200);
    }

    private static void registerEntity(String name, Class<? extends Entity> entityClass, int id, int trackingRange) {
        EntityRegistry.registerModEntity(new ResourceLocation(Adminspace.MODID + ":" + name), entityClass, name, id, Adminspace.instance, trackingRange, 1, true, 0, 0);
    }
}
