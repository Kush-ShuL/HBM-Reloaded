package com.hbm.client.model;

import com.hbm.HbmReloaded;
import com.hbm.client.weapon.pepperbox.GunShaderCompat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/** Optional Sodium bulk submission for the standard entity format. */
final class LegacyObjBulkWriter {
    private static final int STRIDE = 36, CHUNK_VERTICES = 256;
    private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    private record Api(MethodHandle writer, MethodHandle push) { }
    private static final Api API = load();

    private LegacyObjBulkWriter() { }

    private static Api load() {
        try {
            Class<?> type = Class.forName("net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter");
            var lookup = MethodHandles.publicLookup();
            MethodHandle writer = lookup.findStatic(type, "tryOf", MethodType.methodType(type, VertexConsumer.class))
                    .asType(MethodType.methodType(Object.class, VertexConsumer.class));
            MethodHandle push = lookup.findVirtual(type, "push", MethodType.methodType(void.class,
                    MemoryStack.class, long.class, int.class, VertexFormat.class))
                    .asType(MethodType.methodType(void.class, Object.class, MemoryStack.class,
                            long.class, int.class, VertexFormat.class));
            return new Api(writer, push);
        } catch (ClassNotFoundException absent) {
            return null;
        } catch (ReflectiveOperationException | LinkageError unavailable) {
            HbmReloaded.LOGGER.warn("Cannot use the installed Sodium bulk vertex API", unavailable);
            return null;
        }
    }

    /** Returns false before writing anything when this consumer needs the ordinary attribute calls. */
    static boolean render(VertexConsumer consumer, int[] corners, int[] positionIds, int[] normalIds,
            float[] positions, float[] normals, float[] uv, int[] selectedNormals, int[] packedNormals,
            int argb, int overlay, int light) {
        // Iris's scalar world path can recompute quad normals; retain that exact shader-pack behavior.
        if (API == null || corners.length == 0 || GunShaderCompat.shaderPackInUse()) return false;
        try {
            Object writer = (Object) API.writer.invokeExact(consumer);
            if (writer == null) return false;
            for (int id : selectedNormals) {
                int n = id * 3;
                int packed = normal(normals[n]) | normal(normals[n + 1]) << 8 | normal(normals[n + 2]) << 16;
                packedNormals[id] = LITTLE_ENDIAN ? packed : Integer.reverseBytes(packed);
            }
            int color = FastColor.ABGR32.fromArgb32(argb);
            if (!LITTLE_ENDIAN) color = Integer.reverseBytes(color);
            int packedOverlay = LITTLE_ENDIAN ? overlay : Integer.rotateLeft(overlay, 16);
            int packedLight = LITTLE_ENDIAN ? light : Integer.rotateLeft(light, 16);
            try (var stack = MemoryStack.stackPush()) {
                // Every chunk ends at a complete quad, including triangulated source faces.
                long vertices = stack.nmalloc(4, Math.min(CHUNK_VERTICES, corners.length) * STRIDE);
                int scratchPointer = stack.getPointer();
                for (int first = 0; first < corners.length; first += CHUNK_VERTICES) {
                    int count = Math.min(CHUNK_VERTICES, corners.length - first);
                    long address = vertices;
                    for (int index = first; index < first + count; index++, address += STRIDE) {
                        int corner = corners[index];
                        int p = positionIds[corner] * 3;
                        MemoryUtil.memPutFloat(address, positions[p]);
                        MemoryUtil.memPutFloat(address + 4, positions[p + 1]);
                        MemoryUtil.memPutFloat(address + 8, positions[p + 2]);
                        MemoryUtil.memPutInt(address + 12, color);
                        MemoryUtil.memPutFloat(address + 16, uv[corner * 2]);
                        MemoryUtil.memPutFloat(address + 20, uv[corner * 2 + 1]);
                        MemoryUtil.memPutInt(address + 24, packedOverlay);
                        MemoryUtil.memPutInt(address + 28, packedLight);
                        MemoryUtil.memPutInt(address + 32, packedNormals[normalIds[corner]]);
                    }
                    API.push.invokeExact(writer, stack, vertices, count, DefaultVertexFormat.NEW_ENTITY);
                    // A serializer may borrow stack storage, but cannot retain it after push returns.
                    stack.setPointer(scratchPointer);
                }
            }
            return true;
        } catch (RuntimeException | Error failure) {
            throw failure;
        } catch (Throwable failure) {
            // Once push starts, propagating a failed draw avoids submitting any vertices twice.
            throw new IllegalStateException("Cannot submit OBJ vertices through Sodium", failure);
        }
    }

    private static int normal(float value) {
        return (int) (Mth.clamp(value, -1, 1) * 127F) & 255;
    }
}
