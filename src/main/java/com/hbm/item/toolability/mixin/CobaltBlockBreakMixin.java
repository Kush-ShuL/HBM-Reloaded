package com.hbm.item.toolability.mixin;

import com.hbm.item.toolability.CobaltBreaks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Modern replacement for the removed Item.onBlockStartBreak hook, scoped to the actual cobalt item. */
@Mixin(value=ServerPlayerGameMode.class,remap=false)
public abstract class CobaltBlockBreakMixin {
    @Shadow @Final protected ServerPlayer player;
    @Inject(method="destroyBlock",at=@At("HEAD"),cancellable=true)
    private void hbm$cobaltTool(BlockPos pos,CallbackInfoReturnable<Boolean> result){if(CobaltBreaks.breakFromPlayer(player,pos))result.setReturnValue(false);}
}
