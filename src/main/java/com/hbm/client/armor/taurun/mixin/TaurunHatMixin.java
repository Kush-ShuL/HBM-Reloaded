package com.hbm.client.armor.taurun.mixin;

import com.hbm.armor.taurun.TaurunContent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Original ArmorFSB.hides(HAT) applies with just the helmet, independent of full-set bonus. */
@Mixin(value=PlayerRenderer.class,remap=false)
public abstract class TaurunHatMixin {
    @Inject(method="setModelProperties",at=@At("RETURN"))
    private void hbm$taurunHat(AbstractClientPlayer player,CallbackInfo ci){if(player.getItemBySlot(EquipmentSlot.HEAD).is(TaurunContent.HELMET.get()))((PlayerRenderer)(Object)this).getModel().hat.visible=false;}
}
