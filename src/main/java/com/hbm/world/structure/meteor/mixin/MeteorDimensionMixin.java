package com.hbm.world.structure.meteor.mixin;

import com.hbm.world.structure.mixin.StructureManagerAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=ChunkGenerator.class,remap=false)
public abstract class MeteorDimensionMixin {
    @Inject(method="tryGenerateStructure",at=@At("HEAD"),cancellable=true)
    private void hbm$sourceMeteorDimension(StructureSet.StructureSelectionEntry entry,StructureManager manager,RegistryAccess registries,RandomState random,StructureTemplateManager templates,long seed,ChunkAccess chunk,ChunkPos pos,SectionPos section,CallbackInfoReturnable<Boolean> cir){
        if(entry.structure().unwrapKey().filter(key->key.location().getNamespace().equals("hbm")&&key.location().getPath().equals("meteor_dungeon")).isEmpty())return;
        var level=((StructureManagerAccess)manager).hbm$naturalLevel();
        if(!(level instanceof WorldGenLevel world)||!world.getLevel().dimension().equals(Level.OVERWORLD))cir.setReturnValue(false);
    }
}
