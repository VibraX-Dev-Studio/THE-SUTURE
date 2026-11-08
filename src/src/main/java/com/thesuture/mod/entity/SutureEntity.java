package com.thesuture.mod.entity;

import com.thesuture.mod.entity.ai.RelentlessHuntGoal;
import com.thesuture.mod.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SutureEntity extends HostileEntity {
    private static final TrackedData<Boolean> OBSERVED = DataTracker.registerData(SutureEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HUNTING = DataTracker.registerData(SutureEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FRENZIED = DataTracker.registerData(SutureEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int observationCooldown = 0;
    private int boneSnapTimer = 0;

    public SutureEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 60;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 120.0D) // Imposing Colossus
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.34D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0D) // Massive strike
                .add(EntityAttributes.GENERIC_ARMOR, 8.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 110.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.95D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OBSERVED, false);
        this.dataTracker.startTracking(HUNTING, true);
        this.dataTracker.startTracking(FRENZIED, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new RelentlessHuntGoal(this));
    }

    public boolean isObserved() {
        return this.dataTracker.get(OBSERVED);
    }

    public void setObserved(boolean observed) {
        this.dataTracker.set(OBSERVED, observed);
    }

    public boolean isHunting() {
        return this.dataTracker.get(HUNTING);
    }

    public void setHunting(boolean hunting) {
        this.dataTracker.set(HUNTING, hunting);
    }

    public boolean isFrenzied() {
        return this.dataTracker.get(FRENZIED);
    }

    public void setFrenzied(boolean frenzied) {
        this.dataTracker.set(FRENZIED, frenzied);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient()) {
            if (this.observationCooldown > 0) {
                this.observationCooldown--;
                if (this.observationCooldown == 0) {
                    this.setObserved(false);
                    this.setFrenzied(false);
                }
            }

            // Sickening bone dislocation clicks while striding
            boneSnapTimer++;
            if (boneSnapTimer % 35 == 0 && this.getVelocity().horizontalLengthSquared() > 0.005) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.BONE_SNAP, SoundCategory.HOSTILE, 1.2F, 0.7F + this.random.nextFloat() * 0.4F);
            }
        }
    }

    public void markObserved() {
        this.setObserved(true);
        this.setFrenzied(true);
        this.observationCooldown = 30; // 1.5s frenzy window
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Silent stalker footsteps
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUTURE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUTURE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUTURE_DEATH;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
