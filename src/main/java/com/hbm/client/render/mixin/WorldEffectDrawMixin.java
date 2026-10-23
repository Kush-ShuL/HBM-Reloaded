package com.hbm.client.render.mixin;
import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Disjoint finite material registry:171's four gun materials retain their existing capture owner. */
@Mixin(value=RenderType.class,priority=900)
public abstract class WorldEffectDrawMixin {
 @Inject(method="draw",at=@At("HEAD"),cancellable=true)
 private void hbm$sourceWorldEffectMesh(MeshData mesh,CallbackInfo ci){if(WorldEffectMeshes.capture((RenderType)(Object)this,mesh))ci.cancel();}
}
