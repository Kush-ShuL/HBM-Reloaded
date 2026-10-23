package com.hbm.client.weapon.forward.mixin;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(targets="net.minecraft.client.renderer.RenderType$CompositeRenderType",remap=false)
public interface ForwardCompositeTypeAccess {
    @Accessor("state") RenderType.CompositeState hbm$forwardState();
}
