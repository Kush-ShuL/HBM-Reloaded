package com.hbm.client.weapon.forward.mixin;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** The actual native lightmap, including its current updates, without altering a live texture binding. */
@Mixin(value=LightTexture.class,remap=false)
public interface ForwardLightTextureAccess {
    @Accessor("lightTexture") DynamicTexture hbm$nativeLightTexture();
}
