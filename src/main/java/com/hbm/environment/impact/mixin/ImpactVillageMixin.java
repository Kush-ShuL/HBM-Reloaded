package com.hbm.environment.impact.mixin;
import com.hbm.environment.impact.ImpactGeneration;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Actual native village Jigsaw templates only; HBM templates and arbitrary other structures remain untouched. */
@Mixin(value=SinglePoolElement.class,remap=false)
public abstract class ImpactVillageMixin {
    @Shadow @Final protected Either<ResourceLocation,StructureTemplate> template;
    @Inject(method="getSettings",at=@At("RETURN"))
    private void hbm$impactVillage(Rotation rotation,BoundingBox bounds,LiquidSettings liquids,boolean keepJigsaws,CallbackInfoReturnable<StructurePlaceSettings> cir){if(template.left().filter(id->id.getNamespace().equals("minecraft")&&id.getPath().startsWith("village/")).isPresent())cir.getReturnValue().addProcessor(ImpactGeneration.VillageProcessor.INSTANCE);}
}
