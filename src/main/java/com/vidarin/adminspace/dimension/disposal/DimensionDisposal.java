package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.init.BiomeInit;
import com.vidarin.adminspace.init.DimensionInit;
import com.vidarin.adminspace.init.SoundInit;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.network.CPacketSinglePlayerSoundEffect;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MethodsReturnNonnullByDefault
public class DimensionDisposal extends WorldProvider {
    private final ArrayList<EntityPlayerMP> players = new ArrayList<>();
    private final Map<UUID, Integer> ticksInDimension = new HashMap<>();


    public DimensionDisposal() {
        this.biomeProvider = new BiomeProviderSingle(BiomeInit.DISPOSAL_DIM);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionInit.DISPOSAL;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorDisposal(this.world, this.getSeed());
    }

    @Override
    public void onPlayerAdded(@Nonnull EntityPlayerMP player) {
        super.onPlayerAdded(player);
        players.add(player);
        ticksInDimension.put(player.getUniqueID(), 0);
    }

    @Override
    public void onPlayerRemoved(@Nonnull EntityPlayerMP player) {
        super.onPlayerRemoved(player);
        players.remove(player);
        ticksInDimension.remove(player.getUniqueID());
    }

    @Override
    public void onWorldUpdateEntities() {
        super.onWorldUpdateEntities();
        for (EntityPlayerMP player : players) {
            int ticks = ticksInDimension.get(player.getUniqueID());
            if (ticks % 1200 == 0) {
                playDimensionMusic(player);
            }
            ticksInDimension.replace(player.getUniqueID(), ticks + 1);
            player.sendStatusMessage(new TextComponentString("This dimension is not finished."), true);
        }
    }

    private void playDimensionMusic(EntityPlayerMP player) {
        AdminspaceNetworkHandler.INSTANCE.sendTo(new CPacketSinglePlayerSoundEffect(SoundInit.DISPOSAL_MUSIC, 1F, 1F), player);
    }

    @Nullable
    @Override
    public MusicTicker.MusicType getMusicType() {
        return null;
    }
}
