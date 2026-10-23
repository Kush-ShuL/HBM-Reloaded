package com.hbm.item.toolability.mixin;

import com.hbm.item.toolability.SourceAbilityCombat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Source1.7 ItemTool has no modern attack cooldown or attack-speed tooltip modifier. */
@Mixin(value=Player.class,remap=false)
public abstract class CobaltAttackStrengthMixin {
    @Inject(method="getAttackStrengthScale",at=@At("HEAD"),cancellable=true)
    private void hbm$originalToolCombat(float partial,CallbackInfoReturnable<Float> result){if(((Player)(Object)this).getMainHandItem().getItem() instanceof SourceAbilityCombat)result.setReturnValue(1F);}
}
