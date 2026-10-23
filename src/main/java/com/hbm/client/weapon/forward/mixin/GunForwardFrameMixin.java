package com.hbm.client.weapon.forward.mixin;
import com.hbm.client.weapon.forward.GunForwardEffects;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=GameRenderer.class,remap=false)
abstract class GunForwardFrameMixin {
    @Inject(method="render(Lnet/minecraft/client/DeltaTracker;Z)V",at=@At("HEAD"))
    private void hbm$sourceHandFrame(CallbackInfo callback) { GunForwardEffects.beginFrame(); }
    // This is AFTER the complete callee, including all Iris renderLevel tail hooks.
    // Composition already happened before Iris's color-space conversion; discard
    // here also prevents abandoned capture if a pipeline/pack changed mid-frame.
    @Inject(method="render(Lnet/minecraft/client/DeltaTracker;Z)V",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V",shift=At.Shift.AFTER))
    private void hbm$sourceHandFrameDone(CallbackInfo callback) { GunForwardEffects.discardFrame(); }
}
