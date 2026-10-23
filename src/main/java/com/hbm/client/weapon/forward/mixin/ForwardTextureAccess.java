package com.hbm.client.weapon.forward.mixin;
import java.util.Optional;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(value=RenderStateShard.EmptyTextureStateShard.class,remap=false)
public interface ForwardTextureAccess {
    @Invoker("cutoutTexture") Optional<ResourceLocation> hbm$cutoutTexture();
}
