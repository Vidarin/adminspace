package com.vidarin.adminspace.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

import java.util.function.Function;

public interface NBTSerializer<T> {
    NBTSerializer<IBlockState> BLOCK_STATE = create(state -> new NBTTagInt(Block.getStateId(state)), nbt -> Block.getStateById(((NBTTagInt) nbt).getInt()));

    static <T> NBTSerializer<T> create(Function<T, NBTBase> serializer, Function<NBTBase, T> deserializer) {
        return new NBTSerializer<>() {
            @Override
            public NBTBase serialize(T value) {
                return serializer.apply(value);
            }

            @Override
            public T deserialize(NBTBase nbt) {
                return deserializer.apply(nbt);
            }
        };
    }

    NBTBase serialize(T value);
    T deserialize(NBTBase nbt);
}
