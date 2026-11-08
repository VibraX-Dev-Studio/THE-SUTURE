package com.thesuture.mod.entity.ai;

import com.thesuture.mod.entity.SutureEntity;
import com.thesuture.mod.network.ModPackets;
import com.thesuture.mod.sound.ModSounds;
import com.thesuture.mod.util.PerceptionHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class RelentlessHuntGoal extends Goal {
    private final SutureEntity suture;
    private ServerPlayerEntity targetPlayer;
    private int pathUpdateCooldown = 0;
    private int soundCooldown = 0;
    private int whisperCooldown = 0;
    private int screechCooldown = 0;
    private int lightDistortionCooldown = 0;

    public RelentlessHuntGoal(SutureEntity suture) {
        this.suture = suture;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (suture.getWorld().isClient()) {
            return false;
        }

        this.targetPlayer = (ServerPlayerEntity) suture.getWorld().getClosestPlayer(suture, 95.0);
        return this.targetPlayer != null && !this.targetPlayer.isCreative() && !this.targetPlayer.isSpectator();
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void tick() {
        if (targetPlayer == null) return;

        double distanceSq = suture.squaredDistanceTo(targetPlayer);
        double distance = Math.sqrt(distanceSq);

        // Towering colossus gaze lock
        suture.getLookControl().lookAt(targetPlayer, 60.0F, 60.0F);

        // =====================================================================
        // 1. GAZE ACCELERATION MECHANIC:
        // Staring directly at the 4-block behemoth makes it sprint at blinding speed!
        // =====================================================================
        boolean isLookingStraight = PerceptionHelper.isPlayerLookingAt(targetPlayer, suture, 18.0);

        double movementSpeedModifier;
        if (isLookingStraight) {
            suture.markObserved();
            movementSpeedModifier = 2.65D; // Violent berserk charge

            screechCooldown++;
            if (screechCooldown % 20 == 0 && suture.getWorld() instanceof ServerWorld sw) {
                // High-pitched chase screech & unholy roar
                sw.playSound(null, suture.getX(), suture.getY(), suture.getZ(),
                        ModSounds.CHASE_SCREECH, SoundCategory.HOSTILE, 2.2F, 0.85F);
                // Ear ringing tinnitus directly at player
                sw.playSound(null, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                        ModSounds.TINNITUS_SCREECH, SoundCategory.RECORDS, 1.2F, 1.0F);

                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        suture.getX(), suture.getY() + 2.5, suture.getZ(),
                        20, 0.6, 1.2, 0.6, 0.04);
            }
        } else {
            movementSpeedModifier = 1.45D; // Silent stalking colossus

            // Whispering right behind the player's ear
            whisperCooldown++;
            if (whisperCooldown % 35 == 0 && distance < 40.0 && suture.getWorld() instanceof ServerWorld sw) {
                sw.playSound(null, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                        ModSounds.DEMONIC_WHISPER, SoundCategory.HOSTILE, 1.4F, 0.7F + sw.getRandom().nextFloat() * 0.4F);
            }
        }

        pathUpdateCooldown++;
        if (pathUpdateCooldown % 4 == 0) {
            suture.getNavigation().startMovingTo(targetPlayer, movementSpeedModifier);
        }

        // =====================================================================
        // 2. UNSTOPPABLE ESCALATING AUDIO (Louder & faster heartbeats & drones)
        // =====================================================================
        soundCooldown--;
        if (soundCooldown <= 0 && suture.getWorld() instanceof ServerWorld sw) {
            float volume;
            int nextInterval;

            if (distance > 35.0) {
                volume = 0.8F;
                nextInterval = 35;
            } else if (distance > 18.0) {
                volume = 1.4F;
                nextInterval = 18;
            } else if (distance > 7.0) {
                volume = 2.0F;
                nextInterval = 10;
            } else {
                volume = 2.6F;
                nextInterval = 5; // Intense panicked arrhythmia right behind you!
            }

            // Alternate between panic heartbeat and ominous sub-bass drone
            if (distance < 20.0 && sw.getRandom().nextBoolean()) {
                sw.playSound(null, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                        ModSounds.HEARTBEAT_PANIC, SoundCategory.HOSTILE, volume, 0.9F);
            } else {
                sw.playSound(null, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                        ModSounds.PROXIMITY_DRONE, SoundCategory.HOSTILE, volume, 0.7F);
            }
            soundCooldown = nextInterval;
        }

        // =====================================================================
        // 3. LIGHT CORRUPTION: Extinguish nearby torches when close
        // =====================================================================
        lightDistortionCooldown++;
        if (lightDistortionCooldown % 20 == 0 && distance < 12.0 && suture.getWorld() instanceof ServerWorld sw) {
            BlockPos playerHead = targetPlayer.getBlockPos().up();
            for (BlockPos pos : BlockPos.iterate(playerHead.add(-3, -2, -3), playerHead.add(3, 2, 3))) {
                if (sw.getBlockState(pos).isOf(Blocks.TORCH) || sw.getBlockState(pos).isOf(Blocks.WALL_TORCH)) {
                    sw.breakBlock(pos, true);
                    sw.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            15, 0.2, 0.2, 0.2, 0.02);
                    break;
                }
            }
        }

        // =====================================================================
        // 4. SCREEN BLOOD SPLATTERS
        // =====================================================================
        float bloodIntensity = 0.0F;
        if (distance <= 40.0) {
            bloodIntensity = (float) ((40.0 - distance) / 40.0);
            if (isLookingStraight) {
                bloodIntensity = Math.max(bloodIntensity, 0.80F);
            }
        }
        ModPackets.sendBloodProximity(targetPlayer, bloodIntensity);

        // =====================================================================
        // 5. JUMPSCARE MELEE EXECUTION (Within 2.8 blocks for 4-block giant)
        // =====================================================================
        if (distanceSq <= 8.5) {
            executeStrike();
        }
    }

    private void executeStrike() {
        if (targetPlayer != null && suture.getWorld() instanceof ServerWorld sw) {
            // 1. Camera snap into the massive towering jaw
            ModPackets.sendJumpscare(targetPlayer, suture.getX(), suture.getEyeY() - 0.4, suture.getZ());
            ModPackets.sendBloodProximity(targetPlayer, 1.0F);

            // 2. Ear-splitting sonic jumpscare blast
            sw.playSound(
                    null,
                    targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                    ModSounds.JUMPSCARE_STINGER,
                    SoundCategory.RECORDS,
                    3.0F,
                    0.85F
            );

            // 3. Debuff player: Darkness and heavy slowness
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 140, 0, false, false));
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 4, false, false));

            // 4. Heavy strike damage
            targetPlayer.damage(suture.getDamageSources().mobAttack(suture), 20.0F);

            // 5. Colossal ash detonation and retreat
            sw.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    suture.getX(), suture.getY() + 2.0, suture.getZ(),
                    80, 1.0, 2.0, 1.0, 0.08);
            sw.spawnParticles(ParticleTypes.SOUL,
                    suture.getX(), suture.getY() + 1.5, suture.getZ(),
                    30, 0.8, 1.5, 0.8, 0.05);
        }

        suture.discard();
    }
}
