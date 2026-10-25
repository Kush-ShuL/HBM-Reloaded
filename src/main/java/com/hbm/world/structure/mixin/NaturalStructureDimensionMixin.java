package com.hbm.world.structure.mixin;

import com.hbm.world.structure.LegacyNaturalStructure;
import com.hbm.world.structure.LegacyGeneratedStructure;
import com.hbm.world.structure.NaturalStructures;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Native starts and decoration keep the two source policies, without enabling unrelated structures. */
@Mixin(value=ChunkGenerator.class,remap=false)
public abstract class NaturalStructureDimensionMixin {
    @Inject(method="tryGenerateStructure",at=@At("HEAD"),cancellable=true)
    private void hbm$originalDimension(StructureSet.StructureSelectionEntry selected,StructureManager manager,RegistryAccess registries,RandomState random,StructureTemplateManager templates,long seed,ChunkAccess chunk,ChunkPos position,SectionPos section,CallbackInfoReturnable<Boolean> result){
        var structure=selected.structure().value();
        if(!NaturalStructures.allows(structure,manager.shouldGenerateStructures())){result.setReturnValue(false);return;}
        if(!(structure instanceof LegacyNaturalStructure)&&!(structure instanceof LegacyGeneratedStructure))return;
        var level=((StructureManagerAccess)manager).hbm$naturalLevel();
        if(!(level instanceof WorldGenLevel world)||world.getLevel().dimension()!=Level.OVERWORLD)result.setReturnValue(false);
    }

    @WrapOperation(method="applyBiomeDecoration",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/StructureManager;shouldGenerateStructures()Z"))
    private boolean hbm$forcedSourceDecoration(StructureManager manager,Operation<Boolean> original,WorldGenLevel level,ChunkAccess chunk,StructureManager actualManager){
        return original.call(manager)||level.getLevel().dimension()==Level.OVERWORLD&&NaturalStructures.forcesStructures();
    }

    @WrapOperation(method="applyBiomeDecoration",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/StructureManager;startsForStructure(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/levelgen/structure/Structure;)Ljava/util/List;"))
    private List<StructureStart> hbm$onlyAllowedSourceStarts(StructureManager manager,SectionPos section,Structure structure,Operation<List<StructureStart>> original){
        if(!NaturalStructures.allows(structure,manager.shouldGenerateStructures()))return List.of();
        return original.call(manager,section,structure);
    }
}
