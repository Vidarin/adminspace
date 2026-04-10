package com.vidarin.adminspace.util;

import com.vidarin.adminspace.worldgen.genblock.Cube;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.function.Function;

public abstract class NBTSerializer<T> {
    private static final Map<ResourceLocation, NBTSerializer<?>> ALL_SERIALIZERS = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static final NBTSerializer<IBlockState> BLOCK_STATE = create(new ResourceLocation("block_state"), state -> {
        NBTTagCompound compound = new NBTTagCompound();
        IBlockState s = state;
        if (state == null) s = Blocks.AIR.getDefaultState();
        compound.setString("name", Objects.requireNonNull(s.getBlock().getRegistryName()).toString());
        compound.setInteger("meta", s.getBlock().getMetaFromState(s));
        return compound;
    }, nbt -> {
        Block block = Block.getBlockFromName(nbt.getString("name"));
        if (block == null) return Blocks.AIR.getDefaultState();
        return block.getStateFromMeta(nbt.getInteger("meta"));
    });

    public static final NBTSerializer<String> STRING = create(new ResourceLocation("string"), str -> {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("value", str);
        return compound;
    }, nbt -> nbt.getString("value"));

    public static final NBTSerializer<Cube> CUBE_AABB = create(new ResourceLocation("cube_aabb"), cube -> {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setIntArray("from", new int[]{cube.from().getX(), cube.from().getY(), cube.from().getZ()});
        compound.setIntArray("to", new int[]{cube.to().getX(), cube.to().getY(), cube.to().getZ()});
        return compound;
    }, nbt -> {
        int[] from = nbt.getIntArray("from");
        int[] to = nbt.getIntArray("to");
        return new Cube(new CubePos(from[0], from[1], from[2]), new CubePos(to[0], to[1], to[2]));
    });

    /// The one and only serializer serializer that serializes serializers
    public static final NBTSerializer<NBTSerializer<?>> META_SERIALIZER = create(new ResourceLocation("serializer"), serializer -> {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("namespace", serializer.id.getNamespace());
        compound.setString("path", serializer.id.getPath());
        return compound;
    }, nbt -> {
        String path = nbt.getString("path");
        String namespace = nbt.getString("namespace");
        ResourceLocation location = new ResourceLocation(namespace, path);
        NBTSerializer<?> serializer = ALL_SERIALIZERS.get(location);
        if (serializer == null) {
            if (path.endsWith("$list")) {
                ResourceLocation elementLocation = new ResourceLocation(namespace, path.substring(0, path.length() - 5));
                NBTSerializer<?> elementSerializer = ALL_SERIALIZERS.get(elementLocation);
                if (elementSerializer == null) throw new IllegalStateException("Unknown serializer: " + elementLocation);
                else return getListSerializer(elementSerializer);
            } else throw new IllegalStateException("Unknown serializer: " + location);
        }
        return serializer;
    });

    @SuppressWarnings("unchecked")
    public static <T> NBTSerializer<List<T>> getListSerializer(NBTSerializer<T> elementSerializer) {
        ResourceLocation location = new ResourceLocation(elementSerializer.id.getNamespace(), elementSerializer.id.getPath() + "$list");
        if (ALL_SERIALIZERS.containsKey(location)) return (NBTSerializer<List<T>>) ALL_SERIALIZERS.get(location);
        else return create(location, list -> {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList tagList = new NBTTagList();
            for (T t : list) {
                tagList.appendTag(elementSerializer.serialize(t));
            }
            compound.setTag("list", tagList);
            return compound;
        }, nbt -> {
            NBTTagList tagList = nbt.getTagList("list", Constants.NBT.TAG_COMPOUND);
            List<T> list = new ArrayList<>(tagList.tagCount());
            for (int i = 0; i < tagList.tagCount(); i++) {
                list.add(i, elementSerializer.deserialize(tagList.getCompoundTagAt(i)));
            }
            return list;
        });
    }

    private final ResourceLocation id;

    public NBTSerializer(ResourceLocation id) {
        this.id = id;
        if (ALL_SERIALIZERS.containsKey(id)) throw new IllegalArgumentException("NBT Serializer ID already taken");
        else ALL_SERIALIZERS.put(id, this);
    }

    public static <T> NBTSerializer<T> create(ResourceLocation id, Function<T, NBTTagCompound> serializer, Function<NBTTagCompound, T> deserializer) {
        return new NBTSerializer<>(id) {
            @Override
            public NBTTagCompound serialize(T value) {
                return serializer.apply(value);
            }

            @Override
            public T deserialize(NBTTagCompound nbt) {
                return deserializer.apply(nbt);
            }
        };
    }

    public abstract NBTTagCompound serialize(T value);
    public abstract T deserialize(NBTTagCompound nbt);
}
