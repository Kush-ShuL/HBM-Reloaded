package com.hbm.client.armor.t51.mixin;

import com.hbm.armor.t51.T51Content;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Original ArmorFSB.hides(HAT) applies with just the helmet, independent of full-set bonus. */
@Mixin(value=PlayerRenderer.class,remap=false)
public abstract class T51HatMixin {
    @Inject(method="setModelProperties",at=@At("RETURN"))
    private void hbm$t51Hat(AbstractClientPlayer player,CallbackInfo ci){if(player.getItemBySlot(EquipmentSlot.HEAD).is(T51Content.HELMET.get())||player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof com.hbm.weapon.sedna.gun.GunArmorPerks.FullSetArmor armor&&armor.fsbHidesHat())((PlayerRenderer)(Object)this).getModel().hat.visible=false;}
}
