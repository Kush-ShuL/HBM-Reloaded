package com.hbm.client.equipment.npcheadgear.mixin;

import com.hbm.client.equipment.npcheadgear.No9Light;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Only real client block light; skylight and integrated/dedicated server light are unchanged. */
@Mixin(value=LightEngine.class, remap=false)
public abstract class No9LightMixin {
    @Shadow @Final protected LightChunkGetter chunkSource;
    @Inject(method="getLightValue", at=@At("RETURN"), cancellable=true, require=1, allow=1)
    private void hbm$no9(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof BlockLightEngine && chunkSource.getLevel() instanceof ClientLevel level)
            cir.setReturnValue(No9Light.value(level, pos, cir.getReturnValueI()));
    }
}
