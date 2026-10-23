package com.hbm.world.structure.meteor.mixin;

import com.hbm.world.structure.meteor.MeteorTemplateAccess;
import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=SinglePoolElement.class,remap=false)
public abstract class MeteorPoolElementMixin implements MeteorTemplateAccess {
    @Shadow @Final protected Either<ResourceLocation,StructureTemplate> template;
    @Override public StructureTemplate hbm$meteorTemplate(StructureTemplateManager manager){return template.map(manager::getOrCreate,value->value);}
    @Inject(method="getSettings",at=@At("RETURN"))
    private void hbm$actualNeighbourShapes(Rotation rotation,BoundingBox bounds,LiquidSettings liquids,boolean keepJigsaws,CallbackInfoReturnable<StructurePlaceSettings> cir){
        //Legacy stair/fence metadata computed its join geometry while rendering the world;
        //modern blocks store those joins, so let vanilla's final neighbour pass derive them.
        if(template.left().filter(key->key.getNamespace().equals("hbm")&&key.getPath().startsWith("meteor/")).isPresent())cir.getReturnValue().setKnownShape(false);
    }
}
