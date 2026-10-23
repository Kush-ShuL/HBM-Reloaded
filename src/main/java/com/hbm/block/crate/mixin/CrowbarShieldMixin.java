package com.hbm.block.crate.mixin;

import com.hbm.item.legacysword.LegacySwordBlocking;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Sword blocking is the original partial reduction, not modern shield cancellation. */
@Mixin(value=LivingEntity.class,remap=false)
public abstract class CrowbarShieldMixin {
    @Inject(method="isDamageSourceBlocked",at=@At("HEAD"),cancellable=true)
    private void hbm$noShieldRules(DamageSource source,CallbackInfoReturnable<Boolean> cir){
        var entity=(LivingEntity)(Object)this;
        if(entity.getUseItem().getItem() instanceof LegacySwordBlocking)cir.setReturnValue(false);
    }
}
