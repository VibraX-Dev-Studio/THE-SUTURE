package com.thesuture.mod.client.network;

import com.thesuture.mod.client.ui.JumpscareOverlayRenderer;
import com.thesuture.mod.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ClientJumpscareReceiver {

    public static void register() {
        // Jumpscare Camera Snap & Screamer Packet
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.JUMPSCARE_PACKET_ID, (client, handler, buf, responseSender) -> {
            double targetX = buf.readDouble();
            double targetY = buf.readDouble();
            double targetZ = buf.readDouble();

            client.execute(() -> {
                if (client.player == null) return;

                // Snap gaze directly into The Suture's face
                Vec3d eyePos = client.player.getEyePos();
                double dx = targetX - eyePos.x;
                double dy = targetY - eyePos.y;
                double dz = targetZ - eyePos.z;
                double horizontalDist = Math.sqrt(dx * dx + dz * dz);

                float targetYaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
                float targetPitch = (float) (-(MathHelper.atan2(dy, horizontalDist) * (180.0 / Math.PI)));

                client.player.setYaw(targetYaw);
                client.player.setPitch(targetPitch);

                // Flash jumpscare & screen gore
                JumpscareOverlayRenderer.trigger(14);
            });
        });

        // Dynamic Blood Splatter Proximity Packet
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.BLOOD_EFFECT_PACKET_ID, (client, handler, buf, responseSender) -> {
            float bloodIntensity = buf.readFloat();
            client.execute(() -> {
                JumpscareOverlayRenderer.setBloodIntensity(bloodIntensity);
            });
        });
    }
}
