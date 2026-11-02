package com.hbm.client.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.LinkedHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

/** World-only batching for the native immediate source; Iris retains its own batching lifecycle. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class MachineRenderBatches {
    private static Batch active;

    private MachineRenderBatches() { }

    public static MultiBufferSource wrap(BlockEntity blockEntity, MultiBufferSource buffers) {
        if (buffers.getClass() != MultiBufferSource.BufferSource.class
                || buffers != Minecraft.getInstance().renderBuffers().bufferSource()
                || WorldEffectsForward.shadowPass()) return buffers;
        var source = (MultiBufferSource.BufferSource) buffers;
        if (!BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).getNamespace().equals("hbm")) {
            if (active != null && active.delegate == source) active.flush();
            return buffers;
        }
        if (active == null || active.delegate != source) {
            clear();
            active = new Batch(source);
        }
        return active;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void finishBlockEntities(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES && active != null) active.flush();
    }

    @SubscribeEvent
    public static void shutdown(GameShuttingDownEvent event) {
        clear();
    }

    public static void clear() {
        if (active != null) {
            active.close();
            active = null;
        }
    }

    private static int material(RenderType type) {
        return switch (type.name) {
            case "entity_solid", "entity_cutout", "entity_cutout_no_cull", "entity_smooth_cutout" -> 1;
            case "entity_shadow" -> 2;
            default -> 0;
        };
    }

    static final class Batch implements MultiBufferSource, AutoCloseable {
        private final MultiBufferSource.BufferSource delegate;
        private final Buffers batches = new Buffers();

        Batch(MultiBufferSource.BufferSource delegate) {
            this.delegate = delegate;
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            int material = material(type);
            if (material != 0) {
                batches.reserve(type, material);
                // Finish prior ordinary geometry before taking over its draw order.
                delegate.endLastBatch();
                return batches.getBuffer(type);
            }
            // Windows, fluids, decals and custom shaders must see completed opaque depth.
            flush();
            return delegate.getBuffer(type);
        }

        void flush() {
            batches.flush();
        }

        @Override
        public void close() {
            batches.close();
        }
    }

    private static final class Buffers extends MultiBufferSource.BufferSource implements AutoCloseable {
        private static final int MAX_TYPES = 64;
        private final LinkedHashMap<RenderType, Integer> materials = new LinkedHashMap<>(16, .75F, true);

        Buffers() {
            super(new ByteBufferBuilder(1536), new LinkedHashMap<>());
        }

        void reserve(RenderType type, int material) {
            if (materials.get(type) != null) return;
            if (materials.size() == MAX_TYPES) {
                flush();
                var oldest = materials.pollFirstEntry().getKey();
                fixedBuffers.remove(oldest).close();
            }
            fixedBuffers.put(type, new ByteBufferBuilder(type.bufferSize()));
            materials.put(type, material);
        }

        void flush() {
            // Contact decals depth-test against all opaque machines, independent of visitation order.
            for (var entry : materials.entrySet()) if (entry.getValue() == 1) endBatch(entry.getKey());
            for (var entry : materials.entrySet()) if (entry.getValue() == 2) endBatch(entry.getKey());
        }

        @Override
        public void close() {
            startedBuilders.clear();
            fixedBuffers.values().forEach(ByteBufferBuilder::close);
            fixedBuffers.clear();
            materials.clear();
            sharedBuffer.close();
        }
    }
}
