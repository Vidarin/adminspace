package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.main.Adminspace;
import com.vidarin.adminspace.util.MathUtil;
import com.vidarin.adminspace.util.NBTSerializer;
import com.vidarin.adminspace.util.Vec2i;
import com.vidarin.adminspace.worldgen.genblock.GenBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SectorInfo { // 25600x25600 blocks large (1600x1600 chunks)
    public final Vec2i position;
    public final int id;
    public final ActivityLevel activityLevel;
    private final Map<Vec2i, SkyInfo> skyMap;

    public SectorInfo(Vec2i position, ActivityLevel activityLevel) {
        this.position = position;
        this.activityLevel = activityLevel;
        this.id = MathUtil.spiralIndex(position);
        this.skyMap = new HashMap<>();
    }

    private SectorInfo(Vec2i position, ActivityLevel activityLevel, int id, Map<Vec2i, SkyInfo> skyMap) {
        this.position = position;
        this.id = id;
        this.activityLevel = activityLevel;
        this.skyMap = skyMap;
    }

    public SkyInfo createSky(Vec2i position, SkyInfo.SkyState state, GenBlock<IBlockState> genBlock) {
        Adminspace.LOGGER.debug("Created sky at position ({}, {}) with state {}", position.x, position.y, state);
        SkyInfo info = new SkyInfo(position, state, genBlock);
        if (skyMap.containsKey(position)) throw new IllegalArgumentException(String.format("Cannot create sky at (%s, %s), since that position is occupied", position.x, position.y));
        skyMap.put(position, info);
        return info;
    }

    public void updateSkyState(Vec2i position, SkyInfo.SkyState state) {
        if (!skyMap.containsKey(position)) throw new NullPointerException(String.format("No sky found at position (%s, %s)", position.x, position.y));
        skyMap.computeIfPresent(position, (k, info) -> new SkyInfo(position, state, info.id, info.genBlock));
    }

    public SkyInfo getSky(Vec2i position) {
        return skyMap.get(position);
    }

    public boolean hasSky(Vec2i position) {
        return skyMap.containsKey(position);
    }

    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound sectorCompound = new NBTTagCompound();
        sectorCompound.setIntArray("position", new int[]{position.x, position.y});
        sectorCompound.setInteger("id", id);
        sectorCompound.setString("activityLevel", activityLevel.toString());

        NBTTagCompound skyMapCompound = new NBTTagCompound();
        for (Map.Entry<Vec2i, SkyInfo> skyEntry : skyMap.entrySet()){
            String positionString = skyEntry.getKey().x + "," + skyEntry.getKey().y;
            SkyInfo sky = skyEntry.getValue();
            NBTTagCompound skyCompound = new NBTTagCompound();
            skyCompound.setIntArray("position", new int[]{sky.position.x, sky.position.y});
            skyCompound.setInteger("id", sky.id);
            skyCompound.setString("state", sky.state.toString());
            skyCompound.setTag("genBlock", sky.genBlock.toNBT(NBTSerializer.BLOCK_STATE, SkySectorGenBlockDefinition.Symbols.values()));
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
            GenBlock<IBlockState> genBlock = GenBlock.fromNBT(skyCompound.getCompoundTag("genBlock"), NBTSerializer.BLOCK_STATE, SkySectorGenBlockDefinition.Symbols.values());
            skyMap.put(skyPosition, new SkyInfo(skyPosition, skyState, skyId, genBlock));
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
