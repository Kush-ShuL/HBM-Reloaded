package com.hbm.world.structure;

import java.util.Map;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/** Short metadata commits; terrain planning and physical placement never hold a chunk monitor. */
public final class SourceDecorationMetadata {
    public static void publish(ChunkAccess origin,Structure structure,StructureStart start,Map<ChunkPos,ChunkAccess> targets){
        origin=SourceDecorationOwnership.actual(origin);
        synchronized(origin){origin.setStartForStructure(structure,start);}
        if(!start.isValid())return;
        long reference=origin.getPos().toLong();
        for(var entry:targets.entrySet()){
            ChunkAccess target=SourceDecorationOwnership.actual(entry.getValue());
            var position=entry.getKey();
            if(!start.getBoundingBox().intersects(position.getMinBlockX(),position.getMinBlockZ(),position.getMaxBlockX(),position.getMaxBlockZ()))continue;
            synchronized(target){target.addReferenceForStructure(structure,reference);}
        }
    }

    private SourceDecorationMetadata(){}
}
