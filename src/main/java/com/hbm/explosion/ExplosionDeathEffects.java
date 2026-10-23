package com.hbm.explosion;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/** Shared dispatch point for the original ConfettiUtil visual effects as their particle modules are admitted. */
public final class ExplosionDeathEffects {
 private static final List<BiConsumer<LivingEntity,DamageSource>> EFFECTS=new CopyOnWriteArrayList<>();
 private ExplosionDeathEffects(){}
 public static void register(BiConsumer<LivingEntity,DamageSource> handler){EFFECTS.add(handler);}
 public static void decide(LivingEntity entity,DamageSource source){if(!entity.isAlive())for(var effect:EFFECTS)effect.accept(entity,source);}
}
