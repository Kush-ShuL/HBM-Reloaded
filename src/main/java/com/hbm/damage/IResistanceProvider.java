package com.hbm.damage;

import net.minecraft.world.damagesource.DamageSource;

/** Original per-entity threshold/resistance extension (including piercing input). */
public interface IResistanceProvider {
    float[] getCurrentDTDR(DamageSource damage, float amount, float pierceDT, float pierce);
    void onDamageDealt(DamageSource damage, float amount);
}
