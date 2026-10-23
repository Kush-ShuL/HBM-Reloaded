package com.hbm.client.armor.digamma.mixin;

import com.hbm.armor.digamma.DigammaArmorContent;
import com.hbm.armor.t51.T51Runtime;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Source legs.hides(LEFT_LEG,RIGHT_LEG).setFullSetForHide(), explicitly ignoring charge. */
@Mixin(value = PlayerRenderer.class, remap = false)
public abstract class DigammaPlayerPartsMixin {
    @Inject(method = "setModelProperties", at = @At("RETURN"))
    private void hbm$digammaLegs(AbstractClientPlayer player, CallbackInfo callback) {
        if (!player.getItemBySlot(EquipmentSlot.LEGS).is(DigammaArmorContent.LEGS.get()) || !T51Runtime.fullSet(player, false)) return;
        var model = ((PlayerRenderer) (Object) this).getModel();
        model.leftLeg.visible = false; model.rightLeg.visible = false;
        // 1.7 had one skin limb; suppress its modern optional outer skin layer with that same limb.
        model.leftPants.visible = false; model.rightPants.visible = false;
    }
}
