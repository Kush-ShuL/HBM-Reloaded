package com.hbm.world.structure;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;

/** Bootstrap-safe link between the native status chain and the runtime source task. */
public final class SourceDecorationBootstrap {
    public static ChunkStatus status() {
        var status = ChunkStatus.INITIALIZE_LIGHT.getParent();
        if (status.getParent() != ChunkStatus.FEATURES || !status.getName().equals("hbm:source_decoration"))
            throw new IllegalStateException("HBM source decoration is missing from the native chunk status chain");
        return status;
    }

    public static CompletableFuture<ChunkAccess> generate(
            WorldGenContext context, ChunkStep step,
            StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk) {
        return SourceDecoration.generate(context, step, cache, chunk);
    }

    private SourceDecorationBootstrap() {}
}
