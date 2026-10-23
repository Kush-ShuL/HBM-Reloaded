package com.hbm.bomb.nuclear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** A fixed share of each world's configured terrain time for every active worker. */
public final class NuclearTickBudget {
    private static final class Grant {
        final long nanos;
        long deadline;
        boolean started;
        Grant(long nanos) { this.nanos=nanos; }
        long deadline() {
            // Start this worker's clock at its own terrain entry, after unrelated
            // entity work. A repeated request cannot replenish an expired share.
            if(!started) { deadline=System.nanoTime()+nanos;started=true; }
            return deadline;
        }
    }
    private record Frame(long tick,Map<UUID,Grant> grants) { }
    // Values contain UUIDs and numbers only: retaining an entity here would keep
    // its Level alive through this weak map's value -> entity -> Level path.
    private static final WeakHashMap<Level,Frame> FRAMES=new WeakHashMap<>();
    private NuclearTickBudget() { }

    public static long deadline(Level level,NuclearWork worker) {
        if(!(level instanceof ServerLevel server)||!(worker instanceof Entity entity)
                ||entity.level()!=level||entity.isRemoved()||!worker.hasNuclearWork())return System.nanoTime();
        long tick=level.getGameTime();
        Frame frame=FRAMES.get(level);
        if(frame==null||frame.tick()!=tick) {
            var workers=new ArrayList<UUID>();
            for(var active:server.getAllEntities())
                if(!active.isRemoved()&&active instanceof NuclearWork work&&work.hasNuclearWork())workers.add(active.getUUID());
            long total=NuclearConfig.TICK_BUDGET.get()*1_000_000L;
            int count=workers.size();
            long share=total/Math.max(1,count),remainder=total%Math.max(1,count);
            var grants=new HashMap<UUID,Grant>();
            for(int i=0;i<count;i++)grants.put(workers.get(i),new Grant(share+(i<remainder?1:0)));
            frame=new Frame(tick,grants);
            FRAMES.put(level,frame);
        }
        // Admission is fixed for this tick, so grants never exceed the configured
        // total. A worker activated after the snapshot joins on the next tick.
        var grant=frame.grants().get(entity.getUUID());
        return grant==null?System.nanoTime():grant.deadline();
    }
}
