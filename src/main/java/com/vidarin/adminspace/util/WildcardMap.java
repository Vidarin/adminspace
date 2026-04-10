package com.vidarin.adminspace.util;

import com.github.bsideup.jabel.Desugar;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.function.Function;

public class WildcardMap<K> {
    private final Object2ObjectMap<K, Value<?>> innerMap;
    private final NBTSerializer<K> keySerializer;

    public WildcardMap(NBTSerializer<K> keySerializer, int size, float loadFactor) {
        this.innerMap = new Object2ObjectOpenHashMap<>(size, loadFactor);
        this.keySerializer = keySerializer;
    }

    public WildcardMap(NBTSerializer<K> keySerializer, int size) {
        this(keySerializer, size, Hash.DEFAULT_LOAD_FACTOR);
    }

    public WildcardMap(NBTSerializer<K> keySerializer) {
        this(keySerializer, Hash.DEFAULT_INITIAL_SIZE);
    }

    public <V> void put(K key, Class<? super V> clazz, V value, NBTSerializer<V> serializer) {
        this.innerMap.put(key, new Value<>(value, clazz, serializer));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void _putUnsafe(K key, Class<?> clazz, Object value, NBTSerializer<?> serializer) {
        this.innerMap.put(key, new Value(value, clazz, serializer));
    }

    @SuppressWarnings("unchecked")
    public <V> V get(K key, Class<? super V> clazz) {
        Value<?> val = this.innerMap.get(key);
        if (val == null) return null;
        if (clazz.isAssignableFrom(val.clazz)) {
            return (V) val.object;
        } else throw new ClassCastException("Incompatible types; cannot cast " + val.clazz + " to " + clazz);
    }
    
    public <V> void compute(K key, Class<? super V> clazz, NBTSerializer<V> serializer, Function<V, V> func) {
        put(key, clazz, func.apply(get(key, clazz)), serializer);
    }

    public void remove(K key) {
        innerMap.remove(key);
    }

    public int size() {
        return innerMap.size();
    }

    public void clear() {
        innerMap.clear();
    }

    public NBTTagList toNBT() {
        NBTTagList entries = new NBTTagList();
        for (Object2ObjectMap.Entry<K, Value<?>> entry : this.innerMap.object2ObjectEntrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("key", this.keySerializer.serialize(entry.getKey()));
            compound.setTag("value", entry.getValue().serializeObject());
            compound.setTag("serializer", NBTSerializer.META_SERIALIZER.serialize(entry.getValue().serializer));
            entries.appendTag(compound);
        }
        return entries;
    }

    public static <K> WildcardMap<K> fromNBT(NBTSerializer<K> keySerializer, NBTTagList entries) {
        WildcardMap<K> map = new WildcardMap<>(keySerializer, entries.tagCount());
        for (int i = 0; i < entries.tagCount(); i++) {
            NBTTagCompound compound = entries.getCompoundTagAt(i);
            K key = keySerializer.deserialize(compound.getCompoundTag("key"));
            NBTSerializer<?> serializer = NBTSerializer.META_SERIALIZER.deserialize(compound.getCompoundTag("serializer"));
            Object value = serializer.deserialize(compound.getCompoundTag("value"));
            map._putUnsafe(key, value.getClass(), value, serializer);
        }
        return map;
    }

    @Desugar
    private record Value<T>(T object, Class<? super T> clazz, NBTSerializer<T> serializer) {
        NBTTagCompound serializeObject() {
            return serializer.serialize(object);
        }
    }
}
