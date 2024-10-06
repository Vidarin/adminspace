package com.vidarin.adminspace.init;

import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundInit {
    public static SoundEvent VOID_DOOR_OPEN, VOID_DOOR_CLOSE;

    public static void registerSounds() {
        VOID_DOOR_OPEN = registerSound("block.void_door_open");
        VOID_DOOR_CLOSE = registerSound("block.void_door_close");
    }

    private static SoundEvent registerSound(String name) {
        ResourceLocation soundLocation = new ResourceLocation(Adminspace.MODID, name);
        SoundEvent event = new SoundEvent(soundLocation);
        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }
}
