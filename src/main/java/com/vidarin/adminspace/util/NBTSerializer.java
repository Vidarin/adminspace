package com.vidarin.adminspace.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;
import java.util.function.Function;

public interface NBTSerializer<T> {
    @SuppressWarnings("deprecation")
    NBTSerializer<IBlockState> BLOCK_STATE = create(state -> {
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

    static <T> NBTSerializer<T> create(Function<T, NBTTagCompound> serializer, Function<NBTTagCompound, T> deserializer) {
        return new NBTSerializer<>() {
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

    NBTTagCompound serialize(T value);
    T deserialize(NBTTagCompound nbt);
}
