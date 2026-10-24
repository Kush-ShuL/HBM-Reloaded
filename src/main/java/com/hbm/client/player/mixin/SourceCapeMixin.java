package com.hbm.client.player.mixin;

import com.hbm.client.player.SourcePlayerCosmetics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Override the original accessory texture through the native cape layer, retaining its pose and settings. */
@Mixin(value = AbstractClientPlayer.class, remap = false)
public abstract class SourceCapeMixin {
    @Inject(method = "getSkin()Lnet/minecraft/client/resources/PlayerSkin;", at = @At("RETURN"), cancellable = true)
    private void hbm$sourceCape(CallbackInfoReturnable<PlayerSkin> callback) {
        var cape = SourcePlayerCosmetics.cape((AbstractClientPlayer) (Object) this);
        if (cape == null) return;
        var skin = callback.getReturnValue();
        callback.setReturnValue(new PlayerSkin(skin.texture(), skin.textureUrl(), cape,
                skin.elytraTexture(), skin.model(), skin.secure()));
    }
}
