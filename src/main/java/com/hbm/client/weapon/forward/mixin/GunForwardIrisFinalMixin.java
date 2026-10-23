package com.hbm.client.weapon.forward.mixin;
import com.hbm.client.weapon.forward.GunForwardEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** FinalPassRenderer has returned the actual main color target; conversion/GUI have not begun. */
@Pseudo
@Mixin(targets="net.irisshaders.iris.pipeline.IrisRenderingPipeline",remap=false)
abstract class GunForwardIrisFinalMixin {
    @Inject(method="finalizeGameRendering",at=@At("HEAD"),remap=false)
    private void hbm$composeSourceHandEffects(CallbackInfo callback) { GunForwardEffects.finishScene(); }
}
