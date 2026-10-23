package com.hbm.advancement.mixin;

import com.hbm.advancement.HbmAdvancements;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Official1.7 StatFileWriter.func_150871_b refused a child award before its actual source parent. */
@Mixin(PlayerAdvancements.class)
public abstract class SourceParentGateMixin {
    @Shadow private ServerPlayer player;
    @Inject(method="award",at=@At("HEAD"),cancellable=true)
    private void hbm$sourceParent(AdvancementHolder holder,String criterion,CallbackInfoReturnable<Boolean> result){if(!HbmAdvancements.mayAward((PlayerAdvancements)(Object)this,player,holder))result.setReturnValue(false);}
}
