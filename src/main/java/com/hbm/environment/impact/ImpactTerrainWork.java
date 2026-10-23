package com.hbm.environment.impact;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** Three original random chunk attempts, serialized across real asynchronous FULL halos; no new world scanner, budget or chunk backend. */
final class ImpactTerrainWork {
    private static final Map<ServerLevel,Loaded> LEVELS=new WeakHashMap<>();
    private static final class Loaded {
        final LinkedHashMap<Long,LevelChunk> eligible=new LinkedHashMap<>();
        // A chunk loaded solely for our update-neighbour halo is not another source center. Removed on natural player watch/unload.
        final Set<Long> workOnly=new HashSet<>();
        Batch batch;
    }
    private static final class Batch {
        final List<LevelChunk> choices;final ImpactState state;int attempts;Job current;
        Batch(List<LevelChunk> choices,ImpactState state){this.choices=choices;this.state=state;}
        void close(){if(current!=null){current.access.close();current=null;}}
    }
    private static final class Job {
        final LevelChunk source;final NuclearChunkAccess access=new NuclearChunkAccess();int column;BlockPos sample;boolean prepared;
        Job(LevelChunk source){this.source=source;}
        boolean valid(ServerLevel world){var pos=source.getPos();return world.getChunkSource().getChunkNow(pos.x,pos.z)==source;}
        void nextSample(ServerLevel world){
            sample=null;
            while(column<256){int index=column++,x=index>>4,z=index&15;if(world.random.nextBoolean())continue;
                int X=source.getPos().getMinBlockX()+x,Z=source.getPos().getMinBlockZ()+z,top=source.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);
                // Old heightMap used light opacity, not merely non-air. All shape/light neighbours are FULL before this read.
                var scan=new BlockPos.MutableBlockPos(X,top,Z);
                while(top>=world.getMinBuildHeight()&&source.getBlockState(scan.setY(top)).getLightBlock(world,scan)==0)top--;
                top++;
                sample=new BlockPos(X,top-world.random.nextInt(Math.max(1,top-world.getMinBuildHeight())),Z);return;
            }
        }
        boolean ready(ServerLevel world,Loaded loaded){
            boolean ready=true;var center=source.getPos();
            for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){
                int X=center.x+x,Z=center.z+z;
                if(world.getChunkSource().getChunkNow(X,Z)==null)synchronized(LEVELS){loaded.workOnly.add(ChunkPos.asLong(X,Z));}
                if(access.get(world,X,Z)==null)ready=false;
            }
            return ready;
        }
    }
    static void loaded(ServerLevel world,LevelChunk chunk){if(world.dimension()!=Level.OVERWORLD)return;synchronized(LEVELS){var level=LEVELS.computeIfAbsent(world,k->new Loaded());if(!level.workOnly.contains(chunk.getPos().toLong()))level.eligible.put(chunk.getPos().toLong(),chunk);}}
    static void watched(ServerLevel world,LevelChunk chunk){if(world.dimension()!=Level.OVERWORLD)return;synchronized(LEVELS){var level=LEVELS.computeIfAbsent(world,k->new Loaded());level.workOnly.remove(chunk.getPos().toLong());level.eligible.put(chunk.getPos().toLong(),chunk);}}
    static void unloaded(ServerLevel world,ChunkPos pos){synchronized(LEVELS){var level=LEVELS.get(world);if(level==null)return;long key=pos.toLong();level.eligible.remove(key);level.workOnly.remove(key);var batch=level.batch;if(batch!=null&&batch.current!=null&&batch.current.source.getPos().equals(pos)){batch.close();batch.attempts++;}}}
    static void tick(ServerLevel world,ImpactState state){
        if(world.dimension()!=Level.OVERWORLD)return;Loaded loaded;synchronized(LEVELS){loaded=LEVELS.get(world);}if(loaded==null)return;
        synchronized(LEVELS){if(loaded.batch==null){if(!state.active()||loaded.eligible.isEmpty())return;loaded.batch=new Batch(new ArrayList<>(loaded.eligible.values()),state);}}
        var batch=loaded.batch;
        try{
            while(batch.attempts<3){
                if(batch.current==null){
                    // Selection remains in source order: don't select attempt2 until all of attempt1's actual mutations/RNG finish.
                    var job=new Job(batch.choices.get(world.random.nextInt(batch.choices.size())));batch.current=job;
                    if(!job.valid(world)){job.access.close();batch.current=null;batch.attempts++;continue;}
                }
                var job=batch.current;
                if(!job.valid(world)){job.access.close();batch.current=null;batch.attempts++;continue;}
                // Do not draw/replace any random coordinate while waiting for the fixed center halo.
                if(!job.ready(world,loaded))return;
                if(!job.prepared){job.nextSample(world);job.prepared=true;}
                while(job.sample!=null){
                    if(batch.state.dust()>0)ImpactEnvironment.die(world,job.sample,batch.state.dust());
                    if(batch.state.fire()>0)ImpactEnvironment.burn(world,job.sample);
                    job.nextSample(world);
                }
                job.access.close();batch.current=null;batch.attempts++;
            }
            loaded.batch=null;
        }catch(RuntimeException|Error failure){batch.close();loaded.batch=null;throw failure;}
    }
    static void forget(ServerLevel world){Loaded removed;synchronized(LEVELS){removed=LEVELS.remove(world);}if(removed!=null&&removed.batch!=null)removed.batch.close();}
    static void clear(){List<Loaded> levels;synchronized(LEVELS){levels=new ArrayList<>(LEVELS.values());LEVELS.clear();}for(var level:levels)if(level.batch!=null)level.batch.close();}
    private ImpactTerrainWork(){}
}
