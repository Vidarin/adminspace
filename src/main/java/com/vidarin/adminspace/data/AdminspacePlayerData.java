package com.vidarin.adminspace.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdminspacePlayerData {
    public static IData getData(EntityPlayer player) {
        return player.hasCapability(Provider.CAP_PLAYER_DATA, null) ? player.getCapability(Provider.CAP_PLAYER_DATA, null) : Provider.DUMMY;
    }

    public interface IData {
        int getBlindedDuration();
        void setBlindedDuration(int v);
    }

    public static class Data implements IData {
        private int blindedDuration = 0; // Causes the game window to be completely black. Duration is in ticks.

        public Data() {}

        @Override public int getBlindedDuration() { return blindedDuration; }
        @Override public void setBlindedDuration(int v) { blindedDuration = v; }
    }

    public static class Storage implements Capability.IStorage<IData> {
        @Override
        public @Nullable NBTBase writeNBT(Capability<IData> capability, IData instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("blindedDuration", instance.getBlindedDuration());
            return compound;
        }

        @Override
        public void readNBT(Capability<IData> capability, IData instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setBlindedDuration(compound.getInteger("blindedDuration"));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        public static final IData DUMMY = new IData() {
            @Override public int getBlindedDuration() { return 0; }
            @Override public void setBlindedDuration(int v) { }
        };

        @CapabilityInject(IData.class)
        public static Capability<IData> CAP_PLAYER_DATA;

        private final IData INSTANCE = new Data();

        @Override
        public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAP_PLAYER_DATA;
        }

        @Override
        public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? CAP_PLAYER_DATA.cast(INSTANCE) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) CAP_PLAYER_DATA.getStorage().writeNBT(CAP_PLAYER_DATA, INSTANCE, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            CAP_PLAYER_DATA.getStorage().readNBT(CAP_PLAYER_DATA, INSTANCE, null, nbt);
        }
    }
}
