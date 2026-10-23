package com.hbm.world.structure.mixin;

import com.hbm.world.structure.LegacyNaturalStructure;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Original natural structure registrations target dimension0, including worlds reusing overworld biomes. */
@Mixin(value=ChunkGenerator.class,remap=false)
public abstract class NaturalStructureDimensionMixin {
    @Inject(method="tryGenerateStructure",at=@At("HEAD"),cancellable=true)
    private void hbm$originalDimension(StructureSet.StructureSelectionEntry selected,StructureManager manager,RegistryAccess registries,RandomState random,StructureTemplateManager templates,long seed,ChunkAccess chunk,ChunkPos position,SectionPos section,CallbackInfoReturnable<Boolean> result){
        if(!(selected.structure().value() instanceof LegacyNaturalStructure)&&!(selected.structure().value() instanceof com.hbm.world.structure.LegacyGeneratedStructure))return;
        var level=((StructureManagerAccess)manager).hbm$naturalLevel();
        if(!(level instanceof WorldGenLevel world)||world.getLevel().dimension()!=Level.OVERWORLD)result.setReturnValue(false);
    }
}
