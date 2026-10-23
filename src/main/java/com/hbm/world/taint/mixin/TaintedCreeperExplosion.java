package com.hbm.world.taint.mixin;
import com.hbm.world.taint.TaintedCreeper;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**1.7 exposed the creeper explosion virtual method; modern vanilla made it private. */
@Mixin(value=Creeper.class,remap=false)
public abstract class TaintedCreeperExplosion {
 @Inject(method="explodeCreeper",at=@At("HEAD"),cancellable=true) private void hbm$taintedExplosion(CallbackInfo callback){if((Object)this instanceof TaintedCreeper tainted){tainted.sourceExplosion();callback.cancel();}}
}
