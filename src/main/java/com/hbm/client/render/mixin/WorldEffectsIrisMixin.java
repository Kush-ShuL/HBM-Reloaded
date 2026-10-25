package com.hbm.client.render.mixin;
import com.hbm.client.render.WorldEffectsForward;
import com.hbm.client.render.WorldEffectsDepth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Optional Iris1.8 real lifecycle: source colour must not enter encoded material attachments. */
@Pseudo
@Mixin(targets="net.irisshaders.iris.pipeline.IrisRenderingPipeline",remap=false)
public abstract class WorldEffectsIrisMixin {
 @Inject(method="beginLevelRendering",at=@At("RETURN"),remap=false)
 private void hbm$worldEffectsBegin(CallbackInfo ci){WorldEffectsForward.beginWorld();}
 @Inject(method="beginTranslucents",at=@At("HEAD"),remap=false)
 private void hbm$worldEffectsDepth(CallbackInfo ci){WorldEffectsDepth.capture();}
 // Iris unknown-shader handling must not discard our exact source depth shader or rebind its FBO.
 @Inject(method="shouldOverrideShaders",at=@At("HEAD"),cancellable=true,remap=false)
 private void hbm$sourceDepthShader(CallbackInfoReturnable<Boolean> ci){if(WorldEffectsDepth.earlyDepthPass())ci.setReturnValue(false);}
 @Inject(method="renderShadows",at=@At("HEAD"),remap=false)
 private void hbm$worldEffectsShadows(CallbackInfo ci){WorldEffectsForward.shadows(true);}
 @Inject(method="renderShadows",at=@At("RETURN"),remap=false)
 private void hbm$worldEffectsEndShadows(CallbackInfo ci){WorldEffectsForward.shadows(false);}
 @Inject(method="finalizeLevelRendering",at=@At("RETURN"),remap=false)
 private void hbm$worldEffectsFinish(CallbackInfo ci){WorldEffectsForward.finishWorld();}
}
