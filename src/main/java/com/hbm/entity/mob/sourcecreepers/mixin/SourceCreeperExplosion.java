package com.hbm.entity.mob.sourcecreepers.mixin;
import com.hbm.entity.mob.sourcecreepers.SourceCreeper;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Modern private explosion replaces the original virtual func_146077_cc only for this true source family. Tainted hook remains separate. */
@Mixin(value=Creeper.class,remap=false)
public abstract class SourceCreeperExplosion {@Inject(method="explodeCreeper",at=@At("HEAD"),cancellable=true)private void hbm$sourceCreeper(CallbackInfo callback){if((Object)this instanceof SourceCreeper creeper){creeper.sourceExplosion();callback.cancel();}}}
