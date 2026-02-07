package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.MathUtil;
import com.vidarin.adminspace.util.Vec2i;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class SectorInfo { // 25600x25600 blocks large (1600x1600 chunks)
    public final Vec2i position;
    public final int id;
    public final ActivityLevel activityLevel;
    private final Map<Vec2i, SkyInfo> SKY_MAP;

    public SectorInfo(Vec2i position, ActivityLevel activityLevel) {
        this.position = position;
        this.activityLevel = activityLevel;
        this.id = MathUtil.spiralIndex(position);
        this.SKY_MAP = new HashMap<>();
    }

    private SectorInfo(Vec2i position, ActivityLevel activityLevel, int id, Map<Vec2i, SkyInfo> skyMap) {
        this.position = position;
        this.id = id;
        this.activityLevel = activityLevel;
        this.SKY_MAP = skyMap;
    }

    public SkyInfo createSky(Vec2i position, SkyInfo.SkyState state, Supplier<CellTypes[][]> gridSupplier) {
        Adminspace.LOGGER.debug("Created sky at position ({}, {}) with state {}", position.x, position.y, state);
        SkyInfo info = new SkyInfo(position, state, gridSupplier);
        if (SKY_MAP.containsKey(position)) throw new IllegalArgumentException(String.format("Cannot create sky at (%s, %s), since that position is occupied", position.x, position.y));
        SKY_MAP.put(position, info);
        return info;
    }

    public void updateSkyState(Vec2i position, SkyInfo.SkyState state) {
        if (!SKY_MAP.containsKey(position)) throw new NullPointerException(String.format("No sky found at position (%s, %s)", position.x, position.y));
        SKY_MAP.computeIfPresent(position, (k, info) -> new SkyInfo(position, state, info.id, info.bottomGrid, info.middleGrid, info.topGrid));
    }

    public SkyInfo getSky(Vec2i position) {
        return SKY_MAP.get(position);
    }

    public boolean hasSky(Vec2i position) {
        return SKY_MAP.containsKey(position);
    }

    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound sectorCompound = new NBTTagCompound();
        sectorCompound.setIntArray("position", new int[]{position.x, position.y});
        sectorCompound.setInteger("id", id);
        sectorCompound.setString("activityLevel", activityLevel.toString());

        NBTTagCompound skyMapCompound = new NBTTagCompound();
        for (Map.Entry<Vec2i, SkyInfo> skyEntry : SKY_MAP.entrySet()){
            String positionString = skyEntry.getKey().x + "," + skyEntry.getKey().y;
            NBTTagCompound skyCompound = new NBTTagCompound();
            skyCompound.setIntArray("position", new int[]{skyEntry.getValue().position.x, skyEntry.getValue().position.y});
            skyCompound.setInteger("id", skyEntry.getValue().id);
            skyCompound.setString("state", skyEntry.getValue().state.toString());
            skyCompound.setTag("bottomGrid", SkyInfo.writeCellsToNBT(skyEntry.getValue().bottomGrid));
            skyCompound.setTag("middleGrid", SkyInfo.writeCellsToNBT(skyEntry.getValue().middleGrid));
            skyCompound.setTag("topGrid", SkyInfo.writeCellsToNBT(skyEntry.getValue().topGrid));
            skyMapCompound.setTag(positionString, skyCompound);
        }

        sectorCompound.setTag("skyMap", skyMapCompound);
        compound.setTag(String.format("sector_info_%s_%s", position.x, position.y), sectorCompound);
        Adminspace.LOGGER.debug("Saved sector {} to NBT", id);
        return compound;
    }

    public static SectorInfo readFromNBT(@Nonnull NBTTagCompound compound, String mainKey) {
        NBTTagCompound sectorCompound = compound.getCompoundTag(mainKey);
        int[] positionArray = sectorCompound.getIntArray("position");
        Vec2i position = new Vec2i(positionArray[0], positionArray[1]);
        int id = sectorCompound.getInteger("id");
        ActivityLevel activityLevel = ActivityLevel.valueOf(sectorCompound.getString("activityLevel"));

        Map<Vec2i, SkyInfo> skyMap = new HashMap<>();
        NBTTagCompound skyMapCompound = sectorCompound.getCompoundTag("skyMap");
        for (String key : skyMapCompound.getKeySet()) {
            NBTTagCompound skyCompound = skyMapCompound.getCompoundTag(key);
            int[] skyPositionArray = skyCompound.getIntArray("position");
            Vec2i skyPosition = new Vec2i(skyPositionArray[0], skyPositionArray[1]);
            int skyId = skyCompound.getInteger("id");
            SkyInfo.SkyState skyState = SkyInfo.SkyState.valueOf(skyCompound.getString("state"));
            CellTypes[][] bottomGrid = SkyInfo.readCellsFromNBT(skyCompound.getTagList("bottomGrid", 10));
            CellTypes[][] middleGrid = SkyInfo.readCellsFromNBT(skyCompound.getTagList("middleGrid", 10));
            CellTypes[][] topGrid = SkyInfo.readCellsFromNBT(skyCompound.getTagList("topGrid", 10));
            skyMap.put(skyPosition, new SkyInfo(skyPosition, skyState, skyId, bottomGrid, middleGrid, topGrid));
        }

        Adminspace.LOGGER.debug("Read sector {} from NBT", id);
        return new SectorInfo(position, activityLevel, id, skyMap);
    }

    public enum ActivityLevel {
        NONE(0, 0, 0),
        VERY_LOW(0, 2, 0),
        LOW(0, 5, 1),
        MEDIUM(1, 10, 3),
        HIGH(2, 15, 8),
        VERY_HIGH(4, 25, 16),
        EXTREME(7, 40, 24);

        private final int heightVariation;
        private final int squirmingOrganismChance; // of 100
        private final int tentacleChance; // of 500

        ActivityLevel(int heightVariation, int squirmingOrganismChance, int tentacleChance) {
            this.heightVariation = heightVariation;
            this.squirmingOrganismChance = squirmingOrganismChance;
            this.tentacleChance = tentacleChance;
        }

        public int getHeightVariation() {
            return heightVariation;
        }

        public boolean shouldPlaceSquirmingOrganism(Random rand) {
            return squirmingOrganismChance > 0 && rand.nextInt(100 / squirmingOrganismChance) == 0;
        }

        public boolean shouldSpawnTentacle(Random rand) {
            return tentacleChance > 0 && rand.nextInt(500 / tentacleChance) == 0;
        }

        public int getSeverity() {
            return switch (this) {
                case NONE -> 0;
                case VERY_LOW -> 1;
                case LOW -> 2;
                case MEDIUM -> 3;
                case HIGH -> 4;
                case VERY_HIGH -> 5;
                case EXTREME -> 6;
            };
        }
    }
}
