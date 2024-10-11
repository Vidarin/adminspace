package com.vidarin.adminspace.init;

import com.vidarin.adminspace.dimension.skysector.DimensionSkySector;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimensionInit {
    public static final DimensionType SKY_SECTOR = DimensionType.register("Sky Sector", "_sky_sector", 20, DimensionSkySector.class, false);

    public static void registerDimensions() {
        DimensionManager.registerDimension(20, SKY_SECTOR);
    }
}
