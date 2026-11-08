package com.thesuture.mod.entity.ai;

import com.thesuture.mod.entity.SutureEntity;
import com.thesuture.mod.util.PerceptionHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.EnumSet;

public class StalkVantageGoal extends Goal {
    private final SutureEntity suture;
    private ServerPlayerEntity targetPlayer;
    private int gazedTicks = 0;
    private int repositionTimer = 0;

    public StalkVantageGoal(SutureEntity suture) {
        this.suture = suture;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (suture.getWorld().isClient() || suture.isHunting()) {
            return false;
        }

        this.targetPlayer = (ServerPlayerEntity) suture.getWorld().getClosestPlayer(suture, 55.0);
        return this.targetPlayer != null && !this.targetPlayer.isCreative() && !this.targetPlayer.isSpectator();
    }

    @Override
    public boolean shouldContinue() {
        return canStart() && !suture.isHunting();
    }

    @Override
    public void tick() {
        if (targetPlayer == null) return;

        double distanceSq = suture.squaredDistanceTo(targetPlayer);

        // Lock gaze onto target player's eye coordinates
        suture.getLookControl().lookAt(targetPlayer.getX(), targetPlayer.getEyeY(), targetPlayer.getZ(), 30.0F, 30.0F);

        // Check if player is directly looking back at The Suture
        boolean isGazed = PerceptionHelper.isPlayerLookingAt(targetPlayer, suture, 12.0);

        if (isGazed) {
            suture.markObserved();
            gazedTicks++;

            // If player maintains gaze for > 0.8 seconds (16 ticks), vanish into ash
            if (gazedTicks >= 16) {
                vanishIntoAsh();
                return;
            }
        } else {
            gazedTicks = Math.max(0, gazedTicks - 1);

            // If player gets too close (< 8 blocks) and looks away, trigger violent hunt!
            if (distanceSq < 64.0) {
                suture.setHunting(true);
                return;
            }
        }

        // Maintain comfortable stalker distance (20-35 blocks)
        repositionTimer++;
        if (repositionTimer % 40 == 0) {
            if (distanceSq > 1225.0) { // > 35 blocks
                suture.getNavigation().startMovingTo(targetPlayer, 1.0);
            } else if (distanceSq < 400.0) { // < 20 blocks
                // Back away slowly if player approaches without looking
                suture.getNavigation().stop();
            }
        }
    }

    private void vanishIntoAsh() {
        if (suture.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    suture.getX(), suture.getY() + 1.2, suture.getZ(),
                    25, 0.4, 0.8, 0.4, 0.03
            );
        }
        suture.discard();
    }
}
