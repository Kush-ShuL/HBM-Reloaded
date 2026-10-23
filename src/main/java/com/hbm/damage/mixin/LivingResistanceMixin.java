package com.hbm.damage.mixin;

import com.hbm.damage.DamageResistanceHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=LivingEntity.class,remap=false)
public abstract class LivingResistanceMixin {
    @Inject(method="actuallyHurt",at=@At("HEAD"),require=1,allow=1)
    private void hbm$originalResistanceBeforeArmor(DamageSource source,float amount,CallbackInfo callback){
        LivingEntity living=(LivingEntity)(Object)this;
        var stack=((LivingDamageAccess)this).hbm$getDamageContainers();
        if(!stack.isEmpty())DamageResistanceHandler.beforeVanillaDamage(living,source,stack.peek());
    }
}
