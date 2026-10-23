package com.hbm.client.weapon.forward.mixin;
import com.hbm.client.weapon.forward.GunForwardEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Optional exact installed Iris1.8.12 method; no Iris classes link into the common/server runtime. */
@Pseudo
@Mixin(targets="net.irisshaders.iris.pathways.HandRenderer",remap=false)
abstract class GunForwardIrisHandMixin {
    @Inject(method="renderSolid",at=@At("HEAD"),remap=false)
    private void hbm$beginSourceHands(CallbackInfo callback) { GunForwardEffects.beginSolidHand(); }
    @Inject(method="renderSolid",at=@At("RETURN"),remap=false)
    private void hbm$endSourceHands(CallbackInfo callback) { GunForwardEffects.endSolidHand(); }
}
