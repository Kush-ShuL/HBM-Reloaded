package com.hbm.damage.mixin;

import java.util.Stack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Named access to the state formerly public in 1.7.10; no reflection. */
@Mixin(value=LivingEntity.class,remap=false)
public interface LivingDamageAccess {
    @Accessor("lastHurt") float hbm$getLastHurt();
    @Accessor("lastHurt") void hbm$setLastHurt(float value);
    @Accessor("lastHurtByPlayer") void hbm$setLastHurtByPlayer(Player value);
    @Accessor("lastHurtByPlayerTime") void hbm$setLastHurtByPlayerTime(int value);
    @Accessor("damageContainers") Stack<DamageContainer> hbm$getDamageContainers();
    @Accessor("lastDamageSource") void hbm$setLastDamageSource(DamageSource source);
    @Accessor("lastDamageStamp") void hbm$setLastDamageStamp(long stamp);
    @Invoker("getDeathSound") SoundEvent hbm$getDeathSound();
    @Invoker("getHurtSound") SoundEvent hbm$getHurtSound(DamageSource source);
    @Invoker("getSoundVolume") float hbm$getSoundVolume();
    @Invoker("getVoicePitch") float hbm$getVoicePitch();
}
