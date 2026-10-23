package com.hbm.environment.impact.mixin;
import com.hbm.environment.impact.ImpactState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=Level.class,remap=false)
public abstract class ImpactDaytimeMixin {
    @Inject(method="isDay",at=@At("HEAD"),cancellable=true)
    private void hbm$impactNight(CallbackInfoReturnable<Boolean> cir){var world=(Level)(Object)this;if(ImpactState.provider(world)&&ImpactState.get(world).dust()>=.75F)cir.setReturnValue(false);}
}
