package com.hbm.bomb.fishing.mixin;
import com.hbm.bomb.fishing.EntityItemBuoyant;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Only the actual source subtype: do not globally change native item buoyancy or custom fluid backends. */
@Mixin(ItemEntity.class)
public abstract class BuoyantItemWaterMixin {
 @Inject(method="setUnderwaterMovement",at=@At("HEAD"),cancellable=true)
 private void hbm$sourceBuoyantWater(CallbackInfo ci){if((Object)this instanceof EntityItemBuoyant item){item.sourceUnderwaterStep();ci.cancel();}}
}
