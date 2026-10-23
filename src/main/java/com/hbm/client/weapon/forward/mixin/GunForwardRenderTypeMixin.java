package com.hbm.client.weapon.forward.mixin;
import com.hbm.client.weapon.forward.GunForwardEffects;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=RenderType.class,remap=false)
abstract class GunForwardRenderTypeMixin {
    @Inject(method="draw",at=@At("HEAD"),cancellable=true)
    private void hbm$sourceHandMesh(MeshData mesh,CallbackInfo callback) {
        if(GunForwardEffects.capture((RenderType)(Object)this,mesh)) callback.cancel();
    }
}
