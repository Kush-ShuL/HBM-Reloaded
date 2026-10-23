package com.hbm.world.structure.meteor.mixin;

import com.hbm.world.structure.meteor.MeteorDungeonJigsaw;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** A genuine minecraft:jigsaw structure; only its source count/height policy differs. */
@Mixin(value=JigsawStructure.class,remap=false)
public abstract class MeteorNativeJigsawMixin {
    @Shadow @Final private Holder<StructureTemplatePool> startPool;
    @Inject(method="findGenerationPoint",at=@At("HEAD"),cancellable=true)
    private void hbm$meteorSourcePolicy(Structure.GenerationContext context,CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir){
        if(startPool.unwrapKey().filter(key->key.location().equals(MeteorDungeonJigsaw.START_POOL)).isPresent())
            cir.setReturnValue(MeteorDungeonJigsaw.generate(context,MeteorDungeonJigsaw.sourceRandom(context)));
    }
}
