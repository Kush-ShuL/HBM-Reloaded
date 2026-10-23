package com.hbm.world.taint.mixin;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Retain source delayed removal without changing other vanilla falling blocks. */
@Mixin(value=FallingBlockEntity.class,remap=false)
public abstract class TaintFallingTick {
 @Inject(method="tick",at=@At("HEAD"),cancellable=true) private void hbm$removeSourceTaint(CallbackInfo callback){var self=(FallingBlockEntity)(Object)this;if(self.level().isClientSide||!self.getPersistentData().getBoolean("hbmSourceTaintFall"))return;self.getPersistentData().remove("hbmSourceTaintFall");if(self.level().getBlockState(self.blockPosition()).is(self.getBlockState().getBlock()))self.level().removeBlock(self.blockPosition(),false);else{self.discard();callback.cancel();}}
}
