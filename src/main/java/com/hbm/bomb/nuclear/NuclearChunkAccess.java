package com.hbm.bomb.nuclear;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/** Exact-demand chunk requests, with one pending-load limit shared by all nuclear workers in a level. */
public final class NuclearChunkAccess {
    private static final TicketType<UUID> WORK = TicketType.create("hbm_nuclear_work", Comparator.<UUID>naturalOrder());
    private static final int RESIDENT_LIMIT = 64, PENDING_LIMIT = 16;
    // A budget contains no reference back to its weak level key. Futures retain only
    // their lease until completion, including when their owning worker is suspended.
    private static final WeakHashMap<ServerLevel, PendingBudget> PENDING = new WeakHashMap<>();
    private final UUID owner = UUID.randomUUID();
    // Explicit touches on get preserve LRU while peekReady does not change eviction order.
    private final LinkedHashMap<Long, Request> requests = new LinkedHashMap<>(64, .75F);
    private final int residentLimit;
    public NuclearChunkAccess(){this(RESIDENT_LIMIT);}
    public NuclearChunkAccess(int residentLimit){
        if(residentLimit<9)throw new IllegalArgumentException("A nuclear work halo requires at least nine resident chunks");
        this.residentLimit=residentLimit;
    }
    private ServerLevel world;
    private static final class PendingBudget {
        int pending;
        synchronized Lease acquire() {
            if (pending >= PENDING_LIMIT) return null;
            pending++;
            return new Lease(this);
        }
    }
    private static final class Lease {
        final PendingBudget budget;
        boolean released;
        Lease(PendingBudget budget) { this.budget = budget; }
        void release() {
            synchronized (budget) {
                if (!released) { released = true; budget.pending--; }
            }
        }
    }
    private static final class Request {
        final ChunkPos pos;
        CompletableFuture<ChunkResult<ChunkAccess>> future;
        LevelChunk ready;
        Request(ChunkPos pos) { this.pos = pos; }
    }

    public LevelChunk get(ServerLevel level, int x, int z) {
        checkLevel(level);
        world = level;
        long key = ChunkPos.asLong(x, z);
        Request request = requests.get(key);
        if (request == null) request = request(level, x, z);
        // A saturated level creates neither a ticket nor a future for another cold chunk.
        if (request == null) return null;
        requests.remove(key);
        requests.put(key, request);
        LevelChunk chunk = ready(level, key, request, true);
        trim();
        return chunk;
    }

    /** Prepare a mutation's target and its one-chunk neighbour halo without joining generation.
     * The same exact-demand tickets, LRU and shared pending budget own all nine chunks.
     * Callers retain their current operation/cursor when this returns null. */
    public LevelChunk getWithNeighbors(ServerLevel level, int x, int z) {
        LevelChunk center = null;
        boolean complete = true;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                LevelChunk chunk = get(level, x + dx, z + dz);
                if (chunk == null) complete = false;
                else if (dx == 0 && dz == 0) center = chunk;
            }
        }
        return complete ? center : null;
    }

    /** Read existing readiness without requesting/loading/pinning a chunk or touching LRU.
     * Completed requests may be adopted; failures are cleaned up by a subsequent get. */
    public LevelChunk peekReady(ServerLevel level, int x, int z) {
        checkLevel(level);
        long key = ChunkPos.asLong(x, z);
        Request request = requests.get(key);
        return request == null ? level.getChunkSource().getChunkNow(x, z) : ready(level, key, request, false);
    }

    private void checkLevel(ServerLevel level) {
        if (world != null && world != level) throw new IllegalStateException("Nuclear chunk access crossed dimensions");
    }

    private LevelChunk ready(ServerLevel level, long key, Request request, boolean cleanFailure) {
        LevelChunk chunk = request.ready;
        if (chunk == null) chunk = level.getChunkSource().getChunkNow(request.pos.x, request.pos.z);
        if (chunk == null && request.future != null && request.future.isDone()) {
            try {
                var result = request.future.getNow(null);
                if (result != null && result.orElse(null) instanceof LevelChunk loaded) chunk = loaded;
                else if (result != null && cleanFailure) { release(request); requests.remove(key); }
            } catch (CompletionException | CancellationException failure) {
                if (cleanFailure) {
                    release(request); requests.remove(key);
                    com.hbm.HbmReloaded.LOGGER.error("Cannot prepare nuclear work chunk {}", request.pos,
                            failure instanceof CompletionException && failure.getCause() != null ? failure.getCause() : failure);
                }
            }
        }
        if (chunk != null) { request.ready = chunk; request.future = null; }
        return chunk;
    }

    private Request request(ServerLevel level, int x, int z) {
        var source = level.getChunkSource();
        LevelChunk loaded = source.getChunkNow(x, z);
        Lease lease = null;
        if (loaded == null) {
            PendingBudget budget;
            synchronized (PENDING) { budget = PENDING.computeIfAbsent(level, ignored -> new PendingBudget()); }
            lease = budget.acquire();
            if (lease == null) return null;
        }
        var request = new Request(new ChunkPos(x, z));
        boolean ticket = false;
        // A work ticket remains until eviction/completion, rather than expiring while
        // asynchronous generation is still in flight. It does not tick remote terrain.
        try {
            source.addRegionTicket(WORK, request.pos, 0, owner);
            ticket = true;
            request.ready = loaded;
            if (loaded == null) {
                Lease completionLease = lease;
                request.future = CompletableFuture.supplyAsync(
                        () -> source.getChunkFuture(x, z, ChunkStatus.FULL, true)).thenCompose(future -> future);
                request.future.whenComplete((result, failure) -> completionLease.release());
            }
            requests.put(request.pos.toLong(), request);
            return request;
        } catch (RuntimeException failure) {
            if (ticket) source.removeRegionTicket(WORK, request.pos, 0, owner);
            if (lease != null) lease.release();
            throw failure;
        }
    }

    private void trim() {
        if (requests.size() <= residentLimit) return;
        Iterator<Request> iterator = requests.values().iterator();
        while (requests.size() > residentLimit && iterator.hasNext()) {
            Request request = iterator.next();
            if (request.ready == null && (request.future == null || !request.future.isDone())) continue;
            release(request); iterator.remove();
        }
    }

    private void release(Request request) {
        if (world != null) world.getChunkSource().removeRegionTicket(WORK, request.pos, 0, owner);
    }

    public void close() {
        for (var request : requests.values()) release(request);
        // Do not return a pending lease early: removing the owner's ticket does not
        // cancel the actual underlying load. Its completion returns the shared slot.
        requests.clear(); world = null;
    }
}
