package com.hbm.world.structure.tandem.mixin;

import com.hbm.world.structure.tandem.*;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Scope the actual native build, including exceptional exits; manual332 template LOAD does not pass here. */
@Mixin(value=SinglePoolElement.class,remap=false)
public abstract class TandemPoolPlacementMixin {
    @WrapOperation(method="place",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z"))
    private boolean hbm$actualSourceCondition(StructureTemplate template,ServerLevelAccessor level,BlockPos origin,BlockPos pivot,StructurePlaceSettings settings,RandomSource random,int flags,Operation<Boolean> nativePlace){
        boolean inherited=TandemSourceScope.active();String condition=TandemSourceScope.condition();
        if(!inherited)condition=TandemSourcePools.conditionFor(level,(StructurePoolElement)(Object)this);
        if(!inherited&&condition==null)return nativePlace.call(template,level,origin,pivot,settings,random,flags);
        // Keep native-only bookkeeping seeds on the exact native position stream; actual source
        // processors use their caller's real RNG. A scoped single-palette hook prevents an extra world nextInt(1).
        var sourceRandom=inherited?TandemSourceScope.sourceRandom():level.getRandom();
        var nativeHousekeeping=settings.getRandom(origin);settings.setRandom(sourceRandom);
        settings.addProcessor(TandemStructureProcessor.INSTANCE);
        try(var ignored=TandemSourceScope.using(condition,sourceRandom)){return nativePlace.call(template,level,origin,pivot,settings,nativeHousekeeping,flags);}
        finally{settings.setRandom(nativeHousekeeping);} // Later native data-marker bookkeeping must not consume the source stream.
    }
}
