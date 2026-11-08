package com.thesuture.mod.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class JumpscareOverlayRenderer implements HudRenderCallback {
    private static final Identifier RETINAL_FLASH = new Identifier("the_suture", "textures/misc/retinal_flash.png");
    private static final Identifier VIGNETTE = new Identifier("the_suture", "textures/misc/jumpscare_vignette.png");
    private static final Identifier BLOOD_OVERLAY = new Identifier("the_suture", "textures/misc/blood_overlay.png");

    private static int activeFlashTicks = 0;
    private static float bloodIntensity = 0.0F;
    private static float targetBloodIntensity = 0.0F;

    public static void trigger(int durationTicks) {
        activeFlashTicks = durationTicks;
        bloodIntensity = 1.0F;
    }

    public static void setBloodIntensity(float intensity) {
        targetBloodIntensity = Math.min(1.0F, Math.max(0.0F, intensity));
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        // Smooth interpolate blood intensity
        bloodIntensity += (targetBloodIntensity - bloodIntensity) * 0.1F;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // 1. Render Blood Splatters across the screen during hunt / proximity
        if (bloodIntensity > 0.03F) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Heartbeat blood pulse effect
            float pulse = 0.85F + 0.15F * (float) Math.sin((System.currentTimeMillis() % 1000) / 1000.0 * Math.PI * 2.0);
            float alpha = Math.min(1.0F, bloodIntensity * pulse);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            // Draw full-screen blood splatters covering multiple places
            drawContext.drawTexture(BLOOD_OVERLAY, 0, 0, -88, 0.0F, 0.0F, width, height, width, height);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }

        // 2. Render Jumpscare Flash & Screamer
        if (activeFlashTicks > 0) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Dark vignette edge
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.95F);
            drawContext.drawTexture(VIGNETTE, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);

            // High-contrast screaming face flash
            if (activeFlashTicks > 2) {
                float flashAlpha = (activeFlashTicks % 2 == 0) ? 0.98F : 0.70F;
                RenderSystem.setShaderColor(1.0F, 0.85F, 0.85F, flashAlpha);
                drawContext.drawTexture(RETINAL_FLASH, 0, 0, -87, 0.0F, 0.0F, width, height, width, height);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();

            activeFlashTicks--;
        }

        // Naturally decay target blood if no new packets received
        targetBloodIntensity = Math.max(0.0F, targetBloodIntensity - 0.015F);
    }
}
