package com.hbm.radiation;

import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

/** A 16-cubed PRISM region; resistance slices are persisted with the same dose. */
final class PrismRadiationCell {
    static final float MAX_RADIATION = 1_000_000F;
    float radiation, previousRadiation;
    final float[] xResistance = new float[16], yResistance = new float[16], zResistance = new float[16];
    boolean needsRebuild = true, initialized;
    int checksum;

    static float clamp(float radiation) {
        return Float.isNaN(radiation) ? 0F : Math.max(0F, Math.min(radiation, MAX_RADIATION));
    }

    boolean rebuild(ServerLevel level, LevelChunk chunk, int sectionY) {
        needsRebuild = true;
        // The caller supplies an already FULL chunk and FULL neighbour halo. No getChunk/join occurs here.
        var section = chunk.getSection(level.getSectionIndexFromSectionY(sectionY));
        java.util.Arrays.fill(xResistance, 0F);
        java.util.Arrays.fill(yResistance, 0F);
        java.util.Arrays.fill(zResistance, 0F);
        checksum = 0;
        if (!section.hasOnlyAir()) {
            var pos = new BlockPos.MutableBlockPos();
            int ox = chunk.getPos().x << 4, oy = sectionY << 4, oz = chunk.getPos().z << 4;
            for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) {
                var state = section.getBlockState(x, y, z);
                if (state.isAir() || LegacyMaterials.of(state).name().equals("air")) continue;
                pos.set(ox + x, oy + y, oz + z);
                float resistance = Math.min(state.getExplosionResistance(level, pos, null), 100F);
                xResistance[x] += resistance;
                yResistance[y] += resistance;
                zResistance[z] += resistance;
                checksum += (byte) BuiltInRegistries.BLOCK.getId(state.getBlock());
            }
        }
        needsRebuild = false;
        initialized = true;
        return true;
    }

    static int checksum(LevelChunkSection section) {
        if (section.hasOnlyAir()) return 0;
        int checksum = 0;
        // The source intentionally ignores block metadata and uses only the signed low byte of block identity.
        for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) {
            var state = section.getBlockState(x, y, z);
            if (!state.isAir())
                checksum += (byte) BuiltInRegistries.BLOCK.getId(state.getBlock());
        }
        return checksum;
    }

    float resistance(Direction movement) {
        return switch (movement) {
            case EAST -> weighted(xResistance, true);
            case WEST -> weighted(xResistance, false);
            case UP -> weighted(yResistance, true);
            case DOWN -> weighted(yResistance, false);
            case SOUTH -> weighted(zResistance, true);
            case NORTH -> weighted(zResistance, false);
        };
    }

    private static float weighted(float[] values, boolean reverse) {
        float result = 0F;
        for (int i = 1; i < 16; i++) result += values[reverse ? 15 - i : i] / 15F * i;
        return result;
    }

    CompoundTag save(int sectionY) {
        var tag = new CompoundTag();
        tag.putInt("SectionY", sectionY);
        tag.putFloat("Radiation", radiation);
        tag.putBoolean("Initialized", initialized);
        tag.putBoolean("NeedsRebuild", needsRebuild);
        tag.put("XResistance", save(xResistance));
        tag.put("YResistance", save(yResistance));
        tag.put("ZResistance", save(zResistance));
        return tag;
    }

    private static ListTag save(float[] values) {
        var tag = new ListTag();
        for (float value : values) tag.add(FloatTag.valueOf(value));
        return tag;
    }

    static PrismRadiationCell load(CompoundTag tag) {
        var cell = new PrismRadiationCell();
        // Like the source loader, retain the saved float as written. Incoming contributions can
        // transiently exceed the per-own-update cap; loading must not introduce an extra clamp.
        cell.radiation = tag.getFloat("Radiation");
        boolean valid = load(tag, "XResistance", cell.xResistance)
                & load(tag, "YResistance", cell.yResistance) & load(tag, "ZResistance", cell.zResistance);
        cell.initialized = tag.getBoolean("Initialized") && valid;
        cell.needsRebuild = tag.getBoolean("NeedsRebuild") || !cell.initialized;
        // The source does not persist its runtime checksum: the scheduled check rebuilds it after load.
        return cell;
    }

    private static boolean load(CompoundTag tag, String key, float[] destination) {
        ListTag values = tag.getList(key, Tag.TAG_FLOAT);
        if (values.size() != 16) return false;
        for (int i = 0; i < 16; i++) {
            float value = values.getFloat(i);
            if (!Float.isFinite(value)) return false;
            destination[i] = value;
        }
        return true;
    }
}
