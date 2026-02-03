package com.vidarin.adminspace.util;

import com.vidarin.adminspace.data.AdminspacePlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class VisibilityUtil {
    public static boolean isPosVisible(EntityPlayer player, Vec3d target) {
        return isPosVisibleIgnoreRange(player, target, null);
    }

    public static boolean isPosVisibleIgnoreRange(EntityPlayer player, Vec3d target, @Nullable AxisAlignedBB range) {
        if (AdminspacePlayerData.getData(player).getBlindedDuration() > 0) return false;

        World world = player.world;

        Vec3d eyes = player.getPositionEyes(1F);

        RayTraceResult rayTrace = world.rayTraceBlocks(eyes, target, false, true, false);

        if (rayTrace == null || rayTrace.typeOfHit == RayTraceResult.Type.MISS) return true;

        if (range == null) return rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && rayTrace.getBlockPos().equals(new BlockPos(target));

        return rayTrace.typeOfHit == RayTraceResult.Type.BLOCK && (range.contains(rayTrace.hitVec) || rayTrace.getBlockPos().equals(new BlockPos(target)));
    }

    public static boolean isPlayerLookingAt(EntityPlayer player, Vec3d target, double maxAngleRad) {
        Vec3d look = player.getLookVec().normalize();
        Vec3d toTarget = target.subtract(player.getPositionEyes(1.0F)).normalize();

        double cos = look.dotProduct(toTarget);
        return cos > Math.cos(maxAngleRad);
    }

    public static boolean isRangeBeingObserved(World world, AxisAlignedBB range, Accuracy accuracy, double checkRadius, double viewAngleDeg) {
        if (world.getMinecraftServer() == null) return true;

        for (EntityPlayerMP player : world.getMinecraftServer().getPlayerList().getPlayers()) {
            if (player.world != world) continue;

            Vec3i center = new Vec3i((range.maxX + range.minX) / 2, (range.maxY + range.minY) / 2, (range.maxZ + range.minZ) / 2);
            if (player.getPosition().distanceSq(center) > checkRadius * checkRadius) continue;

            if (canPlayerSeeRange(player, Math.toRadians(viewAngleDeg), range, accuracy)) return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static boolean canClientSeeRange(AxisAlignedBB range, Accuracy accuracy) {
        double frustumAngle = 2.0 * Math.atan(
                Math.tan(Math.toRadians(Minecraft.getMinecraft().gameSettings.fovSetting) / 2.0) *
                        ((double) Minecraft.getMinecraft().displayWidth / (double) Minecraft.getMinecraft().displayHeight)
        );

        return canPlayerSeeRange(Minecraft.getMinecraft().player, frustumAngle, range, accuracy);
    }

    public static boolean canPlayerSeeRange(EntityPlayer player, double frustumAngle, AxisAlignedBB range, Accuracy accuracy) {
        double centerX = (range.maxX + range.minX) / 2;
        double centerY = (range.maxY + range.minY) / 2;
        double centerZ = (range.maxZ + range.minZ) / 2;
        Vec3d center = new Vec3d(centerX, centerY, centerZ);

        if (accuracy == Accuracy.Varying) {
            double dist = player.getPositionEyes(1F).squareDistanceTo(center);
            if (dist < 200.0) accuracy = Accuracy.Ultra;
            else if (dist < 1600.0) accuracy = Accuracy.High;
            else if (dist < 25600.0) accuracy = Accuracy.Medium;
            else accuracy = Accuracy.Low;
        }

        if (canPlayerSeePosition(player, center, frustumAngle, range)) return true;
        if (accuracy == Accuracy.Low) return false;

        Vec3d corner_xyz = new Vec3d(range.minX, range.minY, range.minZ);
        Vec3d corner_xyZ = new Vec3d(range.minX, range.minY, range.maxZ);
        Vec3d corner_xYz = new Vec3d(range.minX, range.maxY, range.minZ);
        Vec3d corner_xYZ = new Vec3d(range.minX, range.maxY, range.maxZ);
        Vec3d corner_Xyz = new Vec3d(range.maxX, range.minY, range.minZ);
        Vec3d corner_XyZ = new Vec3d(range.maxX, range.minY, range.maxZ);
        Vec3d corner_XYz = new Vec3d(range.maxX, range.maxY, range.minZ);
        Vec3d corner_XYZ = new Vec3d(range.maxX, range.maxY, range.maxZ);

        if (canPlayerSeePosition(player, corner_xyz, frustumAngle, range) || canPlayerSeePosition(player, corner_xyZ, frustumAngle, range) ||
            canPlayerSeePosition(player, corner_xYz, frustumAngle, range) || canPlayerSeePosition(player, corner_xYZ, frustumAngle, range) ||
            canPlayerSeePosition(player, corner_Xyz, frustumAngle, range) || canPlayerSeePosition(player, corner_XyZ, frustumAngle, range) ||
            canPlayerSeePosition(player, corner_XYz, frustumAngle, range) || canPlayerSeePosition(player, corner_XYZ, frustumAngle, range)) return true;
        if (accuracy == Accuracy.Medium) return false;

        Vec3d side_x = new Vec3d(range.minX, centerY, centerZ);
        Vec3d side_X = new Vec3d(range.maxX, centerY, centerZ);
        Vec3d side_y = new Vec3d(centerX, range.minY, centerZ);
        Vec3d side_Y = new Vec3d(centerX, range.maxY, centerZ);
        Vec3d side_z = new Vec3d(centerX, centerY, range.minZ);
        Vec3d side_Z = new Vec3d(centerX, centerY, range.maxZ);

        if (canPlayerSeePosition(player, side_x, frustumAngle, range) || canPlayerSeePosition(player, side_X, frustumAngle, range) ||
            canPlayerSeePosition(player, side_y, frustumAngle, range) || canPlayerSeePosition(player, side_Y, frustumAngle, range) ||
            canPlayerSeePosition(player, side_z, frustumAngle, range) || canPlayerSeePosition(player, side_Z, frustumAngle, range)) return true;
        if (accuracy == Accuracy.High) return false;

        Vec3d edge_yz = new Vec3d(centerX, range.minY, range.minZ);
        Vec3d edge_yZ = new Vec3d(centerX, range.minY, range.maxZ);
        Vec3d edge_Yz = new Vec3d(centerX, range.maxY, range.minZ);
        Vec3d edge_YZ = new Vec3d(centerX, range.maxY, range.maxZ);
        Vec3d edge_xz = new Vec3d(range.minX, centerY, range.minZ);
        Vec3d edge_xZ = new Vec3d(range.minX, centerY, range.maxZ);
        Vec3d edge_Xz = new Vec3d(range.maxX, centerY, range.minZ);
        Vec3d edge_XZ = new Vec3d(range.maxX, centerY, range.maxZ);
        Vec3d edge_xy = new Vec3d(range.minX, range.minY, centerZ);
        Vec3d edge_xY = new Vec3d(range.minX, range.maxY, centerZ);
        Vec3d edge_Xy = new Vec3d(range.maxX, range.minY, centerZ);
        Vec3d edge_XY = new Vec3d(range.maxX, range.maxY, centerZ);

        return canPlayerSeePosition(player, edge_yz, frustumAngle, range) || canPlayerSeePosition(player, edge_yZ, frustumAngle, range) ||
               canPlayerSeePosition(player, edge_Yz, frustumAngle, range) || canPlayerSeePosition(player, edge_YZ, frustumAngle, range) ||
               canPlayerSeePosition(player, edge_xz, frustumAngle, range) || canPlayerSeePosition(player, edge_xZ, frustumAngle, range) ||
               canPlayerSeePosition(player, edge_Xz, frustumAngle, range) || canPlayerSeePosition(player, edge_XZ, frustumAngle, range) ||
               canPlayerSeePosition(player, edge_xy, frustumAngle, range) || canPlayerSeePosition(player, edge_xY, frustumAngle, range) ||
               canPlayerSeePosition(player, edge_Xy, frustumAngle, range) || canPlayerSeePosition(player, edge_XY, frustumAngle, range);
    }

    public static boolean canPlayerSeePosition(EntityPlayer player, Vec3d target, double frustumAngle, AxisAlignedBB range) {
        return isPlayerLookingAt(player, target, frustumAngle) && isPosVisibleIgnoreRange(player, target, range);
    }

    public enum Accuracy {
        Low, // Checks only the middle
        Medium, // Checks the middle and all 8 corners
        High, // Checks the middle, all 8 corners, and the center of all 6 sides
        Ultra, // Checks the middle, all corners and sides, and the center of all 12 edges

        Varying // Becomes a different value depending on the distance between the player and the object
    }
}
