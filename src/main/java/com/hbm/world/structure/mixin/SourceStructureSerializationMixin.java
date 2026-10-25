package com.hbm.world.structure.mixin;

import com.hbm.world.structure.SourceDecorationOwnership;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;

/** Snapshot native starts, references and the immutable admission stamp together, without region IO under a lock. */
@Mixin(value=ChunkSerializer.class,remap=false)
public abstract class SourceStructureSerializationMixin {
    @WrapMethod(method="write")
    private static CompoundTag hbm$snapshotNativeSourceMetadata(ServerLevel level,ChunkAccess chunk,Operation<CompoundTag> original){
        if(!SourceDecorationOwnership.admitted(chunk))return original.call(level,chunk);
        synchronized(SourceDecorationOwnership.actual(chunk)){return original.call(level,chunk);}
    }
}
