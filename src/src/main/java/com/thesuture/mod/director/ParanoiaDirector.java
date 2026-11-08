package com.thesuture.mod.director;

import com.thesuture.mod.TheSutureMod;
import com.thesuture.mod.entity.SutureEntity;
import com.thesuture.mod.sound.ModSounds;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParanoiaDirector {
    private static final Map<UUID, Integer> paranoiaTicks = new HashMap<>();
    private static int tickCounter = 0;

    public static void tick(ServerWorld world) {
        tickCounter++;
        if (tickCounter % 20 != 0) return; // 1 tick per second

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isCreative() || player.isSpectator()) continue;

            UUID playerId = player.getUuid();
            BlockPos playerPos = player.getBlockPos();
            int light = world.getLightLevel(LightType.BLOCK, playerPos);
            long timeOfDay = world.getTimeOfDay() % 24000;
            boolean isNight = timeOfDay >= 13000 && timeOfDay <= 23000;

            int currentParanoia = paranoiaTicks.getOrDefault(playerId, 0);

            if (light <= 6 || isNight) {
                currentParanoia += 1;

                // 1. Unsettling audio cues in darkness
                if (currentParanoia % 10 == 0) {
                    float roll = world.getRandom().nextFloat();
                    if (roll < 0.45F) {
                        playPhantomFootstepsBehind(world, player);
                    } else if (roll < 0.75F) {
                        playBoneCrackNearby(world, player);
                    } else {
                        playWhisperNearby(world, player);
                    }
                }

                // 2. Spawn colossal Suture (20-30 blocks away)
                if (currentParanoia >= 20) { // ~20 seconds of dark/night
                    boolean alreadyPresent = !world.getEntitiesByClass(
                            SutureEntity.class,
                            new Box(playerPos).expand(70.0),
                            e -> true
                    ).isEmpty();

                    if (!alreadyPresent) {
                        trySpawnColossalSuture(world, player);
                        currentParanoia = 0;
                    }
                }
            } else {
                currentParanoia = Math.max(0, currentParanoia - 1);
            }

            paranoiaTicks.put(playerId, currentParanoia);
        }
    }

    private static void playPhantomFootstepsBehind(ServerWorld world, ServerPlayerEntity player) {
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d behindPos = player.getPos().subtract(lookVec.multiply(2.2));

        world.playSound(
                null,
                behindPos.x, behindPos.y, behindPos.z,
                ModSounds.GHOST_STEP,
                SoundCategory.PLAYERS,
                1.1F,
                0.7F + world.getRandom().nextFloat() * 0.2F
        );
    }

    private static void playBoneCrackNearby(ServerWorld world, ServerPlayerEntity player) {
        Vec3d pos = player.getPos().add(
                (world.getRandom().nextDouble() - 0.5) * 8.0,
                0,
                (world.getRandom().nextDouble() - 0.5) * 8.0
        );

        world.playSound(
                null,
                pos.x, pos.y, pos.z,
                ModSounds.BONE_SNAP,
                SoundCategory.HOSTILE,
                1.4F,
                0.65F + world.getRandom().nextFloat() * 0.3F
        );
    }

    private static void playWhisperNearby(ServerWorld world, ServerPlayerEntity player) {
        world.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                ModSounds.DEMONIC_WHISPER,
                SoundCategory.HOSTILE,
                1.3F,
                0.8F + world.getRandom().nextFloat() * 0.3F
        );
    }

    private static void trySpawnColossalSuture(ServerWorld world, ServerPlayerEntity player) {
        double angle = world.getRandom().nextDouble() * Math.PI * 2.0;
        double dist = 20.0 + world.getRandom().nextDouble() * 10.0;
        int spawnX = (int) (player.getX() + Math.cos(angle) * dist);
        int spawnZ = (int) (player.getZ() + Math.sin(angle) * dist);
        int spawnY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, spawnX, spawnZ);

        BlockPos targetPos = new BlockPos(spawnX, spawnY, spawnZ);

        SutureEntity suture = TheSutureMod.SUTURE_ENTITY.create(world);
        if (suture != null) {
            suture.refreshPositionAndAngles(spawnX + 0.5, spawnY, spawnZ + 0.5, world.getRandom().nextFloat() * 360.0F, 0.0F);
            suture.initialize(world, world.getLocalDifficulty(targetPos), SpawnReason.NATURAL, null, null);
            suture.setTarget(player);
            suture.setHunting(true);
            world.spawnEntity(suture);
        }
    }
}
