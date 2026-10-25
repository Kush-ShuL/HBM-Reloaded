package com.hbm.world.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Admission belongs only to an actual diskless native EMPTY creation, never a load or repair. */
public final class SourceDecorationOwnership {
    private static final int SCHEMA=1;
    private static final DeferredRegister<AttachmentType<?>> TYPES=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    private static final DeferredHolder<AttachmentType<?>,AttachmentType<Integer>> ADMISSION=TYPES.register("source_decoration_admission",()->AttachmentType.builder(()->0).serialize(Codec.INT).build());

    public static void register(IEventBus bus){TYPES.register(bus);}

    public static ChunkAccess actual(ChunkAccess chunk){
        return chunk instanceof ImposterProtoChunk imposter?imposter.getWrapped():chunk;
    }

    public static boolean admitted(ChunkAccess chunk){
        if(!ADMISSION.isBound())return false;
        Integer schema=actual(chunk).getExistingDataOrNull(ADMISSION);
        return schema!=null&&schema==SCHEMA;
    }

    /** Called only from the confirmed diskless createEmptyChunk path. */
    public static void admitNew(ChunkAccess chunk){
        chunk=actual(chunk);
        synchronized(chunk){chunk.setData(ADMISSION,SCHEMA);}
    }

    private SourceDecorationOwnership(){}
}
