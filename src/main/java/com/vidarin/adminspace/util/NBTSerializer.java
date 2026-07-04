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

    public static final NBTSerializer<List<Cube>> CUBE_LIST = create(new ResourceLocation("cube_list"), list -> {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList nbtList = new NBTTagList();
        for (Cube cube : list) {
            NBTTagCompound cubeTag = new NBTTagCompound();
            cubeTag.setIntArray("from", new int[]{cube.from().getX(), cube.from().getY(), cube.from().getZ()});
            cubeTag.setIntArray("to", new int[]{cube.to().getX(), cube.to().getY(), cube.to().getZ()});
            nbtList.appendTag(cubeTag);
        }
        compound.setTag("list", nbtList);
        return compound;
    }, nbt -> {
        NBTTagList nbtList = nbt.getTagList("list", Constants.NBT.TAG_COMPOUND);
        List<Cube> list = new ArrayList<>();
        for (int i = 0; i < nbtList.tagCount(); i++) {
            int[] from = nbt.getIntArray("from");
            int[] to = nbt.getIntArray("to");
            list.add(new Cube(new CubePos(from[0], from[1], from[2]), new CubePos(to[0], to[1], to[2])));
        }
        return list;
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
        if (serializer == null) throw new IllegalStateException("Unknown serializer: " + location);
        return serializer;
    });

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
