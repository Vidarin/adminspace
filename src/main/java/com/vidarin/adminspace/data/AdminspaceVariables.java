package com.vidarin.adminspace.data;

import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminspaceVariables extends WorldSavedData {
    private static final String DATA_NAME = "variables";

    /* MAP REGISTRY (All maps have to be <String, Object>) */
    private static Map<String, Object> PLAYER_AMBIENT_OCCLUSION_SETTING = new HashMap<>();

    public AdminspaceVariables() {
        super(DATA_NAME);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        PLAYER_AMBIENT_OCCLUSION_SETTING = readMapFromNBT(compound, "ambient_occlusion_setting");
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound = writeMapToNBT(compound, PLAYER_AMBIENT_OCCLUSION_SETTING, "ambient_occlusion_setting");
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


    public static AdminspaceVariables get(World world) {
        AdminspaceVariables data = (AdminspaceVariables) world.getPerWorldStorage().getOrLoadData(AdminspaceVariables.class, DATA_NAME);
        if (data == null) {
            data = new AdminspaceVariables();
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }
        return data;
    }

    /* GETTERS AND SETTERS */
    public int getAmbientOcclusionValue(UUID playerId) {
        return (int) PLAYER_AMBIENT_OCCLUSION_SETTING.getOrDefault(playerId.toString(), 0);
    }

    public void setAmbientOcclusionValue(UUID playerId, int value) {
        PLAYER_AMBIENT_OCCLUSION_SETTING.put(playerId.toString(), value);
    }
}
