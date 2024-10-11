package com.vidarin.adminspace.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import java.util.Objects;

public class DimTP extends Teleporter {
    private final WorldServer world;

    private final double x, y, z;

    public DimTP(WorldServer world, double x, double y, double z) {
        super(world);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        this.world.getBlockState(new BlockPos((int) this.x, (int) this.y, (int) this.z));
        entity.setPosition(this.x, this.y, this.z);
        entity.motionX = 0f;
        entity.motionY = 0f;
        entity.motionZ = 0f;
    }

    public static void tpToDimension(EntityPlayer player, int dimensionId, double x, double y, double z) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            MinecraftServer server = player.getEntityWorld().getMinecraftServer();
            WorldServer serverWorld = Objects.requireNonNull(server).getWorld(dimensionId);

            playerMP.mcServer.getPlayerList().transferPlayerToDimension(playerMP, dimensionId, new DimTP(serverWorld, x, y, z));
            player.setPositionAndUpdate(x, y, z);
        }
    }
}
