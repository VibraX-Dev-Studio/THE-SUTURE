package com.thesuture.mod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PerceptionHelper {

    /**
     * Calculates whether a player's line of sight intersects the target's bounding box
     * within a specified field of view cone angle (degrees).
     */
    public static boolean isPlayerLookingAt(ServerPlayerEntity player, LivingEntity target, double coneAngleDegrees) {
        Vec3d eyePos = player.getEyePos();
        Vec3d targetEyePos = target.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d toTarget = targetEyePos.subtract(eyePos).normalize();

        double dot = lookVec.dotProduct(toTarget);
        double threshold = Math.cos(Math.toRadians(coneAngleDegrees));

        if (dot < threshold) {
            return false;
        }

        // Check if there are solid blocks occluding direct line of sight
        RaycastContext context = new RaycastContext(
                eyePos,
                targetEyePos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        );
        BlockHitResult result = player.getWorld().raycast(context);
        return result.getType() == HitResult.Type.MISS;
    }
}
