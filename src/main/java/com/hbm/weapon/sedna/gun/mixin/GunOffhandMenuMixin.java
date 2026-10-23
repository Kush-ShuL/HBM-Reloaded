package com.hbm.weapon.sedna.gun.mixin;
import com.hbm.weapon.sedna.gun.GunHands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** F while hovering a slot uses menu SWAP with inventory button40 rather than Slot.mayPlace on the offhand slot. */
@Mixin(value=AbstractContainerMenu.class,remap=false)
public abstract class GunOffhandMenuMixin {
    @Inject(method="clicked",at=@At("HEAD"),cancellable=true)
    private void hbm$preventOffhandSwap(int slotId,int button,ClickType type,Player player,CallbackInfo callback){var self=(AbstractContainerMenu)(Object)this;if(type==ClickType.SWAP&&button==40&&slotId>=0&&slotId<self.slots.size()&&GunHands.forbiddenOffhand(self.getSlot(slotId).getItem())){callback.cancel();if(!player.level().isClientSide)self.sendAllDataToRemote();}}
}
