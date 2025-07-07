package com.vidarin.adminspace.init;

import com.vidarin.adminspace.dimension.beyond.DimensionBeyond;
import com.vidarin.adminspace.dimension.corridors.DimensionCorridors;
import com.vidarin.adminspace.dimension.deltaquest.DimensionDeltaQuest;
import com.vidarin.adminspace.dimension.disposal.DimensionDisposal;
import com.vidarin.adminspace.dimension.skysector.DimensionSkySector;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimensionInit {
    public static final DimensionType SKY_SECTOR = DimensionType.register("Sky Sector", "_sky_sector", 20, DimensionSkySector.class, false);
    public static final DimensionType CORRIDORS = DimensionType.register("Nowhere", "_corridors", 21, DimensionCorridors.class, false);
    public static final DimensionType DISPOSAL = DimensionType.register("Depository Halls", "_disposal", 22, DimensionDisposal.class, false);
    public static final DimensionType BEYOND = DimensionType.register("The Beyond", "_beyond", 23, DimensionBeyond.class, false);

    public static final DimensionType DELTAQUEST = DimensionType.register("DeltaQuest", "_deltaquest", 100, DimensionDeltaQuest.class, false);

    public static void registerDimensions() {
        DimensionManager.registerDimension(20, SKY_SECTOR);
        DimensionManager.registerDimension(21, CORRIDORS);
        DimensionManager.registerDimension(22, DISPOSAL);
        DimensionManager.registerDimension(23, BEYOND);

        DimensionManager.registerDimension(100, DELTAQUEST);
    }
}
