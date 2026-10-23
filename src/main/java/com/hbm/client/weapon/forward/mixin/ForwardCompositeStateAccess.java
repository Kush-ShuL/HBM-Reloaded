package com.hbm.client.weapon.forward.mixin;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=RenderType.CompositeState.class,remap=false)
public interface ForwardCompositeStateAccess {
    @Accessor("textureState") RenderStateShard.EmptyTextureStateShard hbm$texture();
    @Accessor("shaderState") RenderStateShard.ShaderStateShard hbm$shader();
    @Accessor("transparencyState") RenderStateShard.TransparencyStateShard hbm$transparency();
    @Accessor("depthTestState") RenderStateShard.DepthTestStateShard hbm$depthTest();
    @Accessor("cullState") RenderStateShard.CullStateShard hbm$cull();
    @Accessor("writeMaskState") RenderStateShard.WriteMaskStateShard hbm$writeMask();
}
