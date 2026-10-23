package com.hbm.block.sourcecrystal;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only two original crystal operations and their bounded cold-chunk continuations. Not a general mutation/loader backend. */
public final class CrystalActions extends SavedData {
    static final int[][] NEIGHBOR_ORDER = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
    private static final int[][] SPREAD_ORDER = {{1,0,0},{0,1,0},{0,0,1},{-1,0,0},{0,-1,0},{0,0,-1}};
    private static final Factory<CrystalActions> FACTORY = new Factory<>(CrystalActions::new, CrystalActions::load);
    private static final Map<ServerLevel, CrystalActions> OPEN = new WeakHashMap<>();
    private final List<Work> pending = new ArrayList<>();

    private static final class Work {
        final boolean harden, spread;
        final BlockPos center;
        final int radius;
        int cursor;
        boolean executing;
        NuclearChunkAccess access;
        Work(boolean harden, boolean spread, BlockPos pos, int radius) {
            this.harden = harden; this.spread = spread; center = pos.immutable(); this.radius = radius;
        }
        boolean same(Work other) { return !executing && cursor == 0 && other.cursor == 0 && harden == other.harden && spread == other.spread && radius == other.radius && center.equals(other.center); }
        void close() { try { if (access != null) access.close(); } finally { access = null; } }
    }
    private static CrystalActions data(ServerLevel level) {
        return OPEN.computeIfAbsent(level, key -> key.getDataStorage().computeIfAbsent(FACTORY, "hbm_source_crystal_operations"));
    }
    public static void virus(ServerLevel level, BlockPos pos, boolean spread) {
        data(level).submit(level, new Work(false, spread, pos, 0));
    }
    /** Called only by the one actual ExplosionChaos.hardenVirus leaf. Client source prediction stays cache-local. */
    public static void hardenVirus(Level level, int x, int y, int z, int radius) {
        var center = new BlockPos(x, y, z);
        if (level instanceof ServerLevel server) data(server).submit(server, new Work(true, false, center, radius));
        else sphere(level, new Work(true, false, center, radius), () -> { });
    }
    private void submit(ServerLevel level, Work work) {
        // Deduplicate only identical not-yet-running callbacks while their original cold read is pending.
        // Executing operations remain reentrant: no source nested neighbour action or original method tail is suppressed.
        for (var previous : pending) if (previous.same(work)) return;
        pending.add(work); setDirty(); attempt(level, work);
    }
    private boolean ready(ServerLevel level, Work work) {
        if (work.access == null) work.access = new NuclearChunkAccess();
        var columns = new LinkedHashSet<Long>();
        columns.add(ChunkPos.asLong(work.center.getX() >> 4, work.center.getZ() >> 4));
        if (work.harden) {
            // Project only cells the actual xx/yy/zz sphere will access (yy0 qualifies); no empty10-radius square.
            int r = work.radius, square = r * r / 2;
            for (int xx = -r; xx < r; xx++) for (int zz = -r; zz < r; zz++) if (xx * xx + zz * zz < square)
                columns.add(ChunkPos.asLong((work.center.getX() + xx) >> 4, (work.center.getZ() + zz) >> 4));
        } else for (var offset : NEIGHBOR_ORDER) {
            var pos = work.center.offset(offset[0], offset[1], offset[2]); columns.add(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
        }
        boolean complete = true;
        for (long key : columns) if (work.access.getWithNeighbors(level, ChunkPos.getX(key), ChunkPos.getZ(key)) == null) complete = false;
        return complete;
    }
    private void attempt(ServerLevel level, Work work) {
        if (work.executing) return;
        // A suspended source callback resumes when its own chunk is actually present again; don't load distant centers after restart.
        if (level.getChunkSource().getChunkNow(work.center.getX() >> 4, work.center.getZ() >> 4) == null) { work.close(); return; }
        try {
            if (!ready(level, work)) return;
            work.executing = true;
            try {
                if (work.harden) sphere(level, work, this::setDirty);
                else if (work.spread) spread(level, work);
                else collapse(level, work);
                pending.remove(work); setDirty();
            } finally { work.executing = false; work.close(); }
        } catch (RuntimeException | Error failure) { work.close(); throw failure; }
    }
    private static boolean immune(Block block) {
        // Three modern vanilla air identities all represent the one old Blocks.air.
        // Do not broaden this to arbitrary mod isAir/material/shape/resistance predicates.
        return block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR || block == SourceCrystalsContent.VIRUS.get()
                || block == HbmContent.block("crystal_hardened") || block == SourceCrystalsContent.PULSAR.get();
    }
    private void spread(ServerLevel level, Work work) {
        // Configuration was checked at the actual random-tick entry. Do not abandon a started method tail if the center
        // becomes hardened through its own neighbours, or configuration changes while asynchronous readiness is pending.
        while (work.cursor < 6) {
            var offset = SPREAD_ORDER[work.cursor]; var pos = work.center.offset(offset[0], offset[1], offset[2]);
            if (!immune(level.getBlockState(pos).getBlock())) level.setBlock(pos, SourceCrystalsContent.VIRUS.get().defaultBlockState(), Block.UPDATE_ALL);
            work.cursor++; setDirty();
        }
        // Keep original unconditional final self-conversion; no host recheck discards its source tail.
        level.setBlock(work.center, HbmContent.block("crystal_hardened").defaultBlockState(), Block.UPDATE_ALL);
    }
    private void collapse(ServerLevel level, Work work) {
        for (var offset : NEIGHBOR_ORDER) {
            var pos = work.center.offset(offset[0], offset[1], offset[2]);
            if (!immune(level.getBlockState(pos).getBlock())) return;
        }
        level.setBlock(work.center, HbmContent.block("crystal_hardened").defaultBlockState(), Block.UPDATE_ALL);
    }
    private static void sphere(Level level, Work work, Runnable changed) {
        int r = work.radius, width = r * 2, square = r * r / 2;
        long volume = (long) width * width * width;
        for (; work.cursor < volume; work.cursor++) {
            int xx = work.cursor / (width * width) - r, yy = work.cursor / width % width - r, zz = work.cursor % width - r;
            if (xx * xx + yy * yy + zz * zz < square) {
                var pos = work.center.offset(xx, yy, zz);
                // Only the original client cosmetic branch skips cells absent from its cache.
                // Server reads/writes were all prepared by the same NCA operation above.
                if ((!level.isClientSide || level.hasChunkAt(pos)) && level.getBlockState(pos).is(SourceCrystalsContent.VIRUS.get()))
                    level.setBlock(pos, HbmContent.block("crystal_hardened").defaultBlockState(), Block.UPDATE_ALL);
            }
            changed.run();
        }
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        var jobs = new ListTag();
        for (var work : pending) {
            var row = new CompoundTag(); row.putBoolean("harden", work.harden); row.putBoolean("spread", work.spread);
            row.putLong("center", work.center.asLong()); row.putInt("radius", work.radius); row.putInt("cursor", work.cursor); jobs.add(row);
        }
        tag.put("pending", jobs); return tag;
    }
    private static CrystalActions load(CompoundTag tag, HolderLookup.Provider lookup) {
        var data = new CrystalActions();
        for (var value : tag.getList("pending", Tag.TAG_COMPOUND)) {
            var row = (CompoundTag) value;
            var work = new Work(row.getBoolean("harden"), row.getBoolean("spread"), BlockPos.of(row.getLong("center")), row.getInt("radius"));
            work.cursor = row.getInt("cursor"); data.pending.add(work);
        }
        return data;
    }
    static void loaded(LevelEvent.Load event) { if (event.getLevel() instanceof ServerLevel server) data(server); }
    static void tick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel server)) return;
        var data = data(server);
        for (var work : List.copyOf(data.pending)) if (!work.executing && data.pending.contains(work)) data.attempt(server, work);
    }
    static void unload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel server)) return;
        var data = OPEN.remove(server); if (data != null) for (var work : data.pending) work.close();
    }
    static void stopped(ServerStoppedEvent event) {
        for (var data : OPEN.values()) for (var work : data.pending) work.close(); OPEN.clear();
    }
}
