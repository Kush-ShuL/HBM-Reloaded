package com.hbm.exposure;

import net.minecraft.world.entity.LivingEntity;

/** Armor/filter implementations supply actual source protection, never inferred from item names. */
public interface ExposureProtection {
    enum Hazard { GAS_LUNG, GAS_MONOXIDE, GAS_INERT, PARTICLE_COARSE, PARTICLE_FINE, BACTERIA, GAS_BLISTERING, SAND, LIGHT }
    default boolean protects(LivingEntity entity, Hazard hazard) { return false; }
    default void damageFilter(LivingEntity entity, int damage) {}
    /** Source HazmatRegistry resistance exponent, before pow(10,-resistance). */
    default double radiationResistance(LivingEntity entity) { return 0; }
    /** Allows the source armor provider to preserve float rounding after each worn piece. */
    default float accumulateRadiationResistance(LivingEntity entity, float resistance) {
        return (float) ((double) resistance + radiationResistance(entity));
    }
    default boolean usesCustomGeigerHud(LivingEntity entity) { return false; }
    default boolean radiationImmune(LivingEntity entity) { return false; }
    default boolean digammaImmune(LivingEntity entity) { return false; }
    default boolean digammaProtected(LivingEntity entity) { return false; }
    default boolean seesGas(LivingEntity entity) { return false; }
}
