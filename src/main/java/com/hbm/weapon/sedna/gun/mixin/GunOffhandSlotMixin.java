package com.hbm.weapon.sedna.gun.mixin;
import com.hbm.weapon.sedna.gun.GunHands;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=Slot.class,remap=false)
public abstract class GunOffhandSlotMixin {
    @Inject(method="mayPlace",at=@At("HEAD"),cancellable=true)
    private void hbm$pistolOnlyOffhand(ItemStack stack,CallbackInfoReturnable<Boolean> callback){var self=(Slot)(Object)this;if(self.container instanceof Inventory&&self.getContainerSlot()==40&&GunHands.forbiddenOffhand(stack))callback.setReturnValue(false);}
}
