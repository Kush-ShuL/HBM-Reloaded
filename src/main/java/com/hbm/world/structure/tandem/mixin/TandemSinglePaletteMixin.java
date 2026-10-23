package com.hbm.world.structure.tandem.mixin;

import com.hbm.world.structure.tandem.TandemSourceScope;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Source has one palette. Its native selector must not consume an unrelated extra world.nextInt(1). */
@Mixin(value=StructurePlaceSettings.class,remap=false)
public abstract class TandemSinglePaletteMixin {
    @Inject(method="getRandomPalette",at=@At("HEAD"),cancellable=true)
    private void hbm$sourceSinglePalette(List<?> palettes,BlockPos origin,CallbackInfoReturnable<Object> callback){
        if(TandemSourceScope.active()&&palettes.size()==1)callback.setReturnValue(palettes.getFirst());
    }
}
