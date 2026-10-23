package com.hbm.client.render.mixin;
import com.hbm.client.render.WorldEffectsForward;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(GameRenderer.class)
public abstract class WorldEffectsFrameMixin {
 @Inject(method="render",at=@At("HEAD"))
 private void hbm$beginWorldEffectFrame(CallbackInfo ci){WorldEffectsForward.beginFrame();}
}
