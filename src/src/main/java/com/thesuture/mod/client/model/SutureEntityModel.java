package com.thesuture.mod.client.model;

import com.thesuture.mod.entity.SutureEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SutureEntityModel extends EntityModel<SutureEntity> {
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public SutureEntityModel(ModelPart root) {
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // 1. Broad, towering Torso: 2 blocks wide (8 x 20 x 4)
        ModelPartData body = root.addChild("body",
                ModelPartBuilder.create().uv(32, 16)
                        .cuboid(-4.0F, -18.0F, -2.0F, 8.0F, 20.0F, 4.0F),
                ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        // 2. Colossal Grotesque Head (8 x 9 x 8)
        ModelPartData head = root.addChild("head",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                ModelTransform.pivot(0.0F, -22.0F, -0.5F));

        // 3. Huge Gaping Maw / Unhinged Lower Jaw (8 x 6 x 9)
        head.addChild("jaw",
                ModelPartBuilder.create().uv(0, 16)
                        .cuboid(-4.0F, -2.0F, -5.5F, 8.0F, 6.0F, 9.5F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        // 4. Overly Elongated Reach Arms (Length 38, Width 3)
        root.addChild("right_arm",
                ModelPartBuilder.create().uv(56, 0)
                        .cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 38.0F, 3.0F),
                ModelTransform.pivot(-5.5F, -18.0F, 0.0F));

        root.addChild("left_arm",
                ModelPartBuilder.create().uv(56, 0).mirrored()
                        .cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 38.0F, 3.0F),
                ModelTransform.pivot(5.5F, -18.0F, 0.0F));

        // 5. Massive Stalker Legs (Length 34, Width 3)
        root.addChild("right_leg",
                ModelPartBuilder.create().uv(56, 0)
                        .cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 34.0F, 3.0F),
                ModelTransform.pivot(-2.5F, 0.0F, 0.0F));

        root.addChild("left_leg",
                ModelPartBuilder.create().uv(56, 0).mirrored()
                        .cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 34.0F, 3.0F),
                ModelTransform.pivot(2.5F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(SutureEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float) Math.PI / 180F);
        this.head.pitch = headPitch * ((float) Math.PI / 180F);

        // Towering stalker leg strides
        float legSwing = MathHelper.cos(limbAngle * 0.40F) * 0.95F * limbDistance;
        this.rightLeg.pitch = legSwing;
        this.leftLeg.pitch = -legSwing;
        this.rightArm.pitch = -legSwing * 1.1F;
        this.leftArm.pitch = legSwing * 1.1F;

        // --- DISTORTIONS & HORRIFYING UNHINGED HEAD CONVULSIONS ---
        boolean isGazed = entity.isObserved();
        boolean isFrenzied = entity.isFrenzied();

        if (isGazed || isFrenzied) {
            float convulsionSpeed = isFrenzied ? 4.5F : 2.8F;
            float twitch = MathHelper.sin(animationProgress * convulsionSpeed) * 0.28F;
            float randomSpasm = (entity.getRandom().nextFloat() - 0.5F) * (isFrenzied ? 0.45F : 0.30F);

            // Broken neck: severe lateral tilt and violent cranial vibration
            this.head.roll = 0.55F + twitch + randomSpasm;
            this.head.pitch += Math.abs(randomSpasm * 2.5F);

            // Gaping unhinged jaw: drops all the way down towards chest
            float jawDrop = isFrenzied ? 1.25F : 0.85F;
            this.jaw.pitch = jawDrop + Math.abs(MathHelper.sin(animationProgress * 1.8F) * 0.35F);
            this.jaw.pivotY = 3.0F;

            // Grasping outstretched claws
            this.rightArm.roll = 0.65F + randomSpasm;
            this.leftArm.roll = -0.65F - randomSpasm;
            this.rightArm.pitch = -1.35F + twitch;
            this.leftArm.pitch = -1.35F - twitch;
        } else {
            this.head.roll = 0.0F;
            this.jaw.pitch = 0.15F;
            this.jaw.pivotY = 0.0F;
            this.rightArm.roll = 0.0F;
            this.leftArm.roll = 0.0F;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        rightArm.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        leftArm.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        rightLeg.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        leftLeg.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
