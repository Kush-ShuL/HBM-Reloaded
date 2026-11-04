package com.hbm.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Held-item threshold and resistance contribution, evaluated before armor sets. */
@FunctionalInterface
public interface IProtectiveItem {
    float[] getCurrentDTDR(ItemStack stack, LivingEntity entity, DamageSource damage,
                         float amount, float pierceDT, float pierce);
}
