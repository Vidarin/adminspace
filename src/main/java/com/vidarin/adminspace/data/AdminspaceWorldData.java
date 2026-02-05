package com.vidarin.adminspace.data;

import com.vidarin.adminspace.dimension.skysector.generator.SectorInfo;
import com.vidarin.adminspace.util.Vec2i;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.*;

public class AdminspaceWorldData extends WorldSavedData {
    private static final String DATA_NAME = "adminspace_variables";

    /* REGISTRY */
    private Map<String, Object> PLAYER_AMBIENT_OCCLUSION_SETTING = new HashMap<>();

    private final Map<Vec2i, SectorInfo> SKY_SECTOR_MAP = new HashMap<>();
    private final List<Vec2i> SKY_SECTOR_MAP_KEYLIST = new ArrayList<>();

    private BlockPos BEYOND_SPAWN_POS = null;

    public AdminspaceWorldData() {
        super(DATA_NAME);
    }

    @SuppressWarnings("unused")
    public AdminspaceWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        PLAYER_AMBIENT_OCCLUSION_SETTING = readMapFromNBT(compound, "ambient_occlusion_setting");

        SKY_SECTOR_MAP_KEYLIST.clear();
        SKY_SECTOR_MAP.clear();
        NBTTagList keyList = compound.getTagList("sky_sector_map_keylist", 11);
        for (int i = 0; i < keyList.tagCount(); i++) {
            int[] array = keyList.getIntArrayAt(i);
            Vec2i position = new Vec2i(array[0], array[1]);
            if (!SKY_SECTOR_MAP_KEYLIST.contains(position)) SKY_SECTOR_MAP_KEYLIST.add(position);
            String key = String.format("sector_info_%s_%s", position.x, position.y);
            SKY_SECTOR_MAP.put(position, SectorInfo.readFromNBT(compound, key));
        }

        if (compound.hasKey("beyond_spawn_pos")) {
            int[] beyondSpawnPosArray = compound.getIntArray("beyond_spawn_pos");
            BEYOND_SPAWN_POS = new BlockPos(beyondSpawnPosArray[0], beyondSpawnPosArray[1], beyondSpawnPosArray[2]);
        }
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound = writeMapToNBT(compound, PLAYER_AMBIENT_OCCLUSION_SETTING, "ambient_occlusion_setting");

        for (Map.Entry<Vec2i, SectorInfo> sectorEntry : SKY_SECTOR_MAP.entrySet()) {
            compound = sectorEntry.getValue().writeToNBT(compound);
            if (!SKY_SECTOR_MAP_KEYLIST.contains(sectorEntry.getKey())) SKY_SECTOR_MAP_KEYLIST.add(sectorEntry.getKey());
        }
        NBTTagList keyList = new NBTTagList();
        for (Vec2i key : SKY_SECTOR_MAP_KEYLIST) {
            NBTTagIntArray keyTag = new NBTTagIntArray(new int[]{key.x, key.y});
            keyList.appendTag(keyTag);
        }
        compound.setTag("sky_sector_map_keylist", keyList);

        if (BEYOND_SPAWN_POS != null) compound.setIntArray("beyond_spawn_pos", new int[]{BEYOND_SPAWN_POS.getX(), BEYOND_SPAWN_POS.getY(), BEYOND_SPAWN_POS.getZ()});
        return compound;
    }

    private NBTTagCompound writeMapToNBT(@Nonnull NBTTagCompound compound, Map<String, Object> map, String name) {
        if (map == null) return compound;

        NBTTagCompound mapCompound = new NBTTagCompound();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) continue;

            if (value instanceof String)
                mapCompound.setString(entry.getKey(), (String) value);
            else if (value instanceof Integer)
                mapCompound.setInteger(entry.getKey(), (int) value);
            else if (value instanceof Float)
                mapCompound.setFloat(entry.getKey(), (float) value);
            else if (value instanceof Boolean)
                mapCompound.setBoolean(entry.getKey(), (boolean) value);
        }
        compound.setTag(name, mapCompound);
        return compound;
    }

    private Map<String, Object> readMapFromNBT(@Nonnull NBTTagCompound compound, String name) {
        Map<String, Object> map = new HashMap<>();

        if (compound.hasKey(name, 10)) {
            NBTTagCompound mapCompound = compound.getCompoundTag(name);
            for (String key : mapCompound.getKeySet()) {
                NBTBase tag = mapCompound.getTag(key);

                if (tag instanceof NBTTagString) {
                    map.put(key, mapCompound.getString(key));
                } else if (tag instanceof NBTTagInt) {
                    map.put(key, mapCompound.getInteger(key));
                } else if (tag instanceof NBTTagFloat) {
                    map.put(key, mapCompound.getFloat(key));
                } else if (tag instanceof NBTTagByte) {
                    map.put(key, mapCompound.getBoolean(key));
                } else {
                    map.put(key, tag.toString());
                }
            }
        }

        return map;
    }


    public static AdminspaceWorldData get(World world) {
        AdminspaceWorldData data = (AdminspaceWorldData) world.getPerWorldStorage().getOrLoadData(AdminspaceWorldData.class, DATA_NAME);
        if (data == null) {
            data = new AdminspaceWorldData();
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }
        return data;
    }

    /* GETTERS AND SETTERS */
    public int getAmbientOcclusionValue(UUID playerId) { return (int) PLAYER_AMBIENT_OCCLUSION_SETTING.getOrDefault(playerId.toString(), 0); }
    public void setAmbientOcclusionValue(UUID playerId, int value) { PLAYER_AMBIENT_OCCLUSION_SETTING.put(playerId.toString(), value); }

    public Map<Vec2i, SectorInfo> getSkySectorMap() { return SKY_SECTOR_MAP; }
    public void putSectorToMap(Vec2i key, SectorInfo value) { SKY_SECTOR_MAP.put(key, value); }

    public boolean hasSetBeyondSpawnPos() { return BEYOND_SPAWN_POS != null; }
    public BlockPos getBeyondSpawnPos() { return BEYOND_SPAWN_POS; }
    public void setBeyondSpawnPos(BlockPos pos) { BEYOND_SPAWN_POS = pos; }
}
