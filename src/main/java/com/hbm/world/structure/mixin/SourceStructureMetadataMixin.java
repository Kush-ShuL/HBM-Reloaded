package com.hbm.world.structure.mixin;

import com.hbm.world.structure.SourceDecorationOwnership;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;

/** Source-stage metadata publication cannot race native start/ref iterations on fresh admitted chunks. */
@Mixin(value=ChunkAccess.class,remap=false)
public abstract class SourceStructureMetadataMixin {
    @WrapMethod(method="getStartForStructure")
    private StructureStart hbm$readStart(Structure structure,Operation<StructureStart> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk))return original.call(structure);
        synchronized(chunk){return original.call(structure);}
    }
    @WrapMethod(method="setStartForStructure")
    private void hbm$writeStart(Structure structure,StructureStart start,Operation<Void> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk)){original.call(structure,start);return;}
        synchronized(chunk){original.call(structure,start);}
    }
    @WrapMethod(method="getAllStarts")
    private Map<Structure,StructureStart> hbm$snapshotStarts(Operation<Map<Structure,StructureStart>> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk))return original.call();
        synchronized(chunk){return Collections.unmodifiableMap(new HashMap<>(original.call()));}
    }
    @WrapMethod(method="setAllStarts")
    private void hbm$writeStarts(Map<Structure,StructureStart> starts,Operation<Void> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk)){original.call(starts);return;}
        synchronized(chunk){original.call(starts);}
    }
    @WrapMethod(method="getReferencesForStructure")
    private LongSet hbm$snapshotReferences(Structure structure,Operation<LongSet> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk))return original.call(structure);
        synchronized(chunk){return LongSets.unmodifiable(new LongOpenHashSet(original.call(structure)));}
    }
    @WrapMethod(method="addReferenceForStructure")
    private void hbm$writeReference(Structure structure,long reference,Operation<Void> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk)){original.call(structure,reference);return;}
        synchronized(chunk){original.call(structure,reference);}
    }
    @WrapMethod(method="getAllReferences")
    private Map<Structure,LongSet> hbm$snapshotAllReferences(Operation<Map<Structure,LongSet>> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk))return original.call();
        synchronized(chunk){
            var result=new HashMap<Structure,LongSet>();
            // Native Proto->Level promotion passes these values to setAllReferences;
            // each detached set must remain mutable for native subsequent additions.
            original.call().forEach((structure,references)->result.put(structure,new LongOpenHashSet(references)));
            return Collections.unmodifiableMap(result);
        }
    }
    @WrapMethod(method="setAllReferences")
    private void hbm$writeAllReferences(Map<Structure,LongSet> references,Operation<Void> original){
        var chunk=(ChunkAccess)(Object)this;
        if(!SourceDecorationOwnership.admitted(chunk)){original.call(references);return;}
        synchronized(chunk){original.call(references);}
    }
}
