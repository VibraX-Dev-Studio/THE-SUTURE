package com.thesuture.mod.client;

import com.thesuture.mod.TheSutureMod;
import com.thesuture.mod.client.model.SutureEntityModel;
import com.thesuture.mod.client.network.ClientJumpscareReceiver;
import com.thesuture.mod.client.render.SutureEntityRenderer;
import com.thesuture.mod.client.ui.JumpscareOverlayRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class TheSutureClientMod implements ClientModInitializer {
    public static final EntityModelLayer SUTURE_MODEL_LAYER = new EntityModelLayer(
            new Identifier(TheSutureMod.MOD_ID, "suture"),
            "main"
    );

    @Override
    public void onInitializeClient() {
        // 1. Register custom entity model layer
        EntityModelLayerRegistry.registerModelLayer(SUTURE_MODEL_LAYER, SutureEntityModel::getTexturedModelData);

        // 2. Register entity renderer with emissive glow
        EntityRendererRegistry.register(TheSutureMod.SUTURE_ENTITY, SutureEntityRenderer::new);

        // 3. Register client S2C network packet handler
        ClientJumpscareReceiver.register();

        // 4. Register jumpscare HUD overlay renderer
        HudRenderCallback.EVENT.register(new JumpscareOverlayRenderer());
    }
}
