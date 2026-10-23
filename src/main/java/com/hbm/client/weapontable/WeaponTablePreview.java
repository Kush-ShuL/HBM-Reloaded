package com.hbm.client.weapontable;

import com.hbm.client.weapon.pepperbox.GunFirstPersonRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

/** GUI only: center the actual selected source parts/attachments before the user's view rotation. */
public final class WeaponTablePreview implements MultiBufferSource {
    private static final int COLOR = 1, UV = 2, OVERLAY = 4, LIGHT = 8, NORMAL = 16;
    private final List<Batch> batches = new ArrayList<>();
    private int used;
    private float minX, minY, minZ, maxX, maxY, maxZ;

    public void draw(GunFirstPersonRenderer renderer, ItemStack stack, int configuration, PoseStack view,
            MultiBufferSource buffers, int light, int overlay, float partial) {
        used = 0;
        minX = minY = minZ = Float.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
        // Execute the real source callback exactly once. Its complete setupModTable and part transforms
        // remain intact; the GUI's translation/yaw/pitch have not been applied to these vertices.
        renderer.renderModificationTable(stack, configuration, new PoseStack(), this, light, overlay, partial);
        if (minX == Float.POSITIVE_INFINITY) return;
        float cx = (minX + maxX) * .5F, cy = (minY + maxY) * .5F, cz = (minZ + maxZ) * .5F;
        for (int batch = 0; batch < used; batch++) batches.get(batch).replay(view.last(), buffers, cx, cy, cz);
    }

    @Override public VertexConsumer getBuffer(RenderType type) {
        if (used > 0 && batches.get(used - 1).type == type) return batches.get(used - 1);
        if (used == batches.size()) batches.add(new Batch());
        var batch = batches.get(used++);
        batch.type = type; batch.count = 0; batch.misc.clear();
        return batch;
    }

    private record Extra(VertexFormatElement element, int[] data) { }
    private final class Batch implements VertexConsumer {
        private RenderType type;
        private int count;
        // Reused primitive storage:8 float position/UV/normal,6 int mask/color/overlay/light per vertex.
        private float[] data = new float[8 * 256];
        private int[] attributes = new int[6 * 256];
        private final Map<Integer, List<Extra>> misc = new HashMap<>();
        private int current() {
            if (count == 0) throw new IllegalStateException("Weapon preview attribute before vertex");
            return count - 1;
        }
        private void mask(int flag) { attributes[current() * 6] |= flag; }
        @Override public VertexConsumer addVertex(float x, float y, float z) {
            if (count * 8 == data.length) {
                data = Arrays.copyOf(data, data.length * 2);
                attributes = Arrays.copyOf(attributes, attributes.length * 2);
            }
            int offset = count * 8;
            data[offset] = x; data[offset + 1] = y; data[offset + 2] = z;
            attributes[count * 6] = 0;
            count++;
            minX = Math.min(minX, x); minY = Math.min(minY, y); minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x); maxY = Math.max(maxY, y); maxZ = Math.max(maxZ, z);
            return this;
        }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) {
            mask(COLOR); attributes[current() * 6 + 1] = (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
            return this;
        }
        @Override public VertexConsumer setUv(float u, float v) {
            mask(UV); int offset = current() * 8; data[offset + 3] = u; data[offset + 4] = v; return this;
        }
        @Override public VertexConsumer setUv1(int u, int v) {
            mask(OVERLAY); int offset = current() * 6; attributes[offset + 2] = u; attributes[offset + 3] = v; return this;
        }
        @Override public VertexConsumer setUv2(int u, int v) {
            mask(LIGHT); int offset = current() * 6; attributes[offset + 4] = u; attributes[offset + 5] = v; return this;
        }
        @Override public VertexConsumer setNormal(float x, float y, float z) {
            mask(NORMAL); int offset = current() * 8; data[offset + 5] = x; data[offset + 6] = y; data[offset + 7] = z; return this;
        }
        @Override public VertexConsumer misc(VertexFormatElement element, int... rawData) {
            misc.computeIfAbsent(current(), ignored -> new ArrayList<>()).add(new Extra(element, rawData.clone()));
            return this;
        }
        private void replay(PoseStack.Pose pose, MultiBufferSource buffers, float cx, float cy, float cz) {
            if (count == 0) return;
            var output = buffers.getBuffer(type);
            for (int vertex = 0; vertex < count; vertex++) {
                int f = vertex * 8, a = vertex * 6, flags = attributes[a];
                output.addVertex(pose, data[f] - cx, data[f + 1] - cy, data[f + 2] - cz);
                if ((flags & COLOR) != 0) output.setColor(attributes[a + 1]);
                if ((flags & UV) != 0) output.setUv(data[f + 3], data[f + 4]);
                if ((flags & OVERLAY) != 0) output.setUv1(attributes[a + 2], attributes[a + 3]);
                if ((flags & LIGHT) != 0) output.setUv2(attributes[a + 4], attributes[a + 5]);
                if ((flags & NORMAL) != 0) output.setNormal(pose, data[f + 5], data[f + 6], data[f + 7]);
                var extra = misc.get(vertex);
                if (extra != null) for (var value : extra) output.misc(value.element(), value.data());
            }
        }
    }
}
