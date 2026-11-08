package com.thesuture.mod.client.render;

import com.thesuture.mod.client.TheSutureClientMod;
import com.thesuture.mod.client.model.SutureEntityModel;
import com.thesuture.mod.entity.SutureEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SutureEntityRenderer extends MobEntityRenderer<SutureEntity, SutureEntityModel> {
    private static final Identifier TEXTURE = new Identifier("the_suture", "textures/entity/suture_body.png");
    private static final Identifier GLOW_EYES = new Identifier("the_suture", "textures/entity/suture_eyes_glow.png");

    public SutureEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SutureEntityModel(context.getPart(TheSutureClientMod.SUTURE_MODEL_LAYER)), 0.9F);

        // Add emissive eyes and needle teeth that pierce the darkness
        this.addFeature(new EyesFeatureRenderer<>(this) {
            @Override
            public RenderLayer getEyesTexture() {
                return RenderLayer.getEyes(GLOW_EYES);
            }
        });
    }

    @Override
    protected void setupTransforms(SutureEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        // Scale to 4+ blocks tall and 2 blocks wide!
        matrices.scale(1.45F, 1.45F, 1.45F);
    }

    @Override
    public Identifier getTexture(SutureEntity entity) {
        return TEXTURE;
    }
}
