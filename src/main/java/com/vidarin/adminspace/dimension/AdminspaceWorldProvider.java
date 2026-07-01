package com.vidarin.adminspace.dimension;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.CPacketSinglePlayerSoundEffect;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public abstract class AdminspaceWorldProvider extends WorldProvider {
    public final List<EntityPlayerMP> players = new ObjectArrayList<>();
    public final Object2IntMap<UUID> ticksInDimension = new Object2IntArrayMap<>();
    protected Map<UUID, DimensionMusic> playerMusic = new Object2ObjectOpenHashMap<>();
    protected Object2IntMap<UUID> lastPlayed = new Object2IntOpenHashMap<>();

    @Override
    public void onPlayerAdded(@Nonnull EntityPlayerMP player) {
        super.onPlayerAdded(player);
        players.add(player);
        ticksInDimension.put(player.getUniqueID(), 0);
        lastPlayed.put(player.getUniqueID(), 0);
    }

    @Override
    public void onPlayerRemoved(@Nonnull EntityPlayerMP player) {
        super.onPlayerRemoved(player);
        players.remove(player);
        ticksInDimension.remove(player.getUniqueID());
        playerMusic.remove(player.getUniqueID());
        lastPlayed.remove(player.getUniqueID());
    }

    @Override
    public void onWorldUpdateEntities() {
        for (EntityPlayerMP player : players) {
            int ticks = ticksInDimension.get(player.getUniqueID());
            DimensionMusic currentMusic = playerMusic.get(player.getUniqueID());
            if (ticks >= 0 && (currentMusic == null || ticks - lastPlayed.get(player.getUniqueID()) >= currentMusic.delay)) {
                DimensionMusic m = playDimensionMusic(world.rand, player);
                AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketSinglePlayerSoundEffect(m.sound, m.vol, m.pitch), player);
                playerMusic.put(player.getUniqueID(), m);
                lastPlayed.put(player.getUniqueID(), ticks);
            }
            ticksInDimension.replace(player.getUniqueID(), ticks + 1);
        }
    }

    @NotNull
    public abstract DimensionMusic playDimensionMusic(Random rand, EntityPlayer player);

    @Nullable
    @Override
    public MusicTicker.MusicType getMusicType() {
        return null;
    }

    @Desugar
    public record DimensionMusic(SoundEvent sound, float vol, float pitch, int delay) {}
}
