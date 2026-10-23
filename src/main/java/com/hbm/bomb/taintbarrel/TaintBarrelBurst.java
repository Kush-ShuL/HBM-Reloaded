package com.hbm.bomb.taintbarrel;

import com.hbm.bomb.conventional.PrimedExplosive;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.world.taint.TaintBlock;
import com.hbm.world.taint.TaintContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only the original RedBarrel taint callback's cold continuation. Sole NCA prepares its bounded9x9 footprint, not a new blast engine. */
public final class TaintBarrelBurst extends SavedData {
    private static final Factory<TaintBarrelBurst> FACTORY=new Factory<>(TaintBarrelBurst::new,TaintBarrelBurst::load);
    private static final Map<ServerLevel,TaintBarrelBurst> OPEN=new WeakHashMap<>();
    private final List<Work> pending=new ArrayList<>();
    private static final class Work {
        final double x,y,z;final BlockPos center;
        PrimedExplosive source;
        int cursor,metadata=-1;BlockPos sample;
        boolean blastDone,executing,tracked;
        NuclearChunkAccess access;
        Work(double x,double y,double z,PrimedExplosive source){this.x=x;this.y=y;this.z=z;center=BlockPos.containing(x,y,z);this.source=source;}
        void close(){try{if(access!=null)access.close();}finally{access=null;}}
        boolean ready(ServerLevel level){
            int minX=(center.getX()-4)>>4,maxX=(center.getX()+4)>>4,minZ=(center.getZ()-4)>>4,maxZ=(center.getZ()+4)>>4;
            boolean warm=true;for(int x=minX-1;x<=maxX+1;x++)for(int z=minZ-1;z<=maxZ+1;z++)if(level.getChunkSource().getChunkNow(x,z)==null)warm=false;
            if(warm)return true;if(access==null)access=new NuclearChunkAccess();boolean ready=true;
            for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++)if(access.getWithNeighbors(level,x,z)==null)ready=false;
            return ready;
        }
    }
    private static TaintBarrelBurst data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_taint_barrel_bursts"));}
    public static void explode(ServerLevel level,double x,double y,double z,PrimedExplosive source){var data=data(level);var work=new Work(x,y,z,source);if(data.attempt(level,work))return;work.tracked=true;data.pending.add(work);data.setDirty();}
    private void changed(Work work){if(work.tracked)setDirty();}
    private boolean attempt(ServerLevel level,Work work){
        if(work.executing)return false;
        if(level.getChunkSource().getChunkNow(work.center.getX()>>4,work.center.getZ()>>4)==null){work.close();return false;}
        try{
            if(!work.ready(level))return false;work.executing=true;
            try{
                if(!work.blastDone){level.explode(work.source,work.x,work.y,work.z,1F,false,Level.ExplosionInteraction.NONE);work.blastDone=true;work.source=null;changed(work);}
                // Do not pre-draw coordinates or metadata before the native source explosion or the readiness boundary.
                while(work.cursor<100){
                    if(work.sample==null){work.sample=work.center.offset(level.random.nextInt(9)-4,level.random.nextInt(9)-4,level.random.nextInt(9)-4);changed(work);}
                    if(work.metadata<0){var before=level.getBlockState(work.sample);
                        // Original BlockTaint inherits normal-cube rendering despite its separate0.75 collision box.
                        boolean normal=before.is(TaintContent.TAINT.get())||LegacyBlockTraits.isNormalCube(level,work.sample,before);
                        if(normal&&!before.isAir()){work.metadata=level.random.nextInt(3)+4;changed(work);}
                    }
                    if(work.metadata>=0)level.setBlock(work.sample,TaintContent.TAINT.get().defaultBlockState().setValue(TaintBlock.META,work.metadata),2);
                    work.sample=null;work.metadata=-1;work.cursor++;changed(work);
                }
                return true;
            }finally{work.executing=false;work.source=null;work.close();}
        }catch(RuntimeException|Error failure){work.close();throw failure;}
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){var list=new ListTag();for(var work:pending){var row=new CompoundTag();row.putDouble("x",work.x);row.putDouble("y",work.y);row.putDouble("z",work.z);row.putInt("cursor",work.cursor);row.putBoolean("blastDone",work.blastDone);if(work.sample!=null){row.putLong("sample",work.sample.asLong());row.putInt("metadata",work.metadata);}list.add(row);}tag.put("pending",list);return tag;}
    private static TaintBarrelBurst load(CompoundTag tag,HolderLookup.Provider lookup){var data=new TaintBarrelBurst();for(var entry:tag.getList("pending",Tag.TAG_COMPOUND)){var row=(CompoundTag)entry;var work=new Work(row.getDouble("x"),row.getDouble("y"),row.getDouble("z"),null);work.cursor=row.getInt("cursor");work.blastDone=row.getBoolean("blastDone");if(row.contains("sample")){work.sample=BlockPos.of(row.getLong("sample"));work.metadata=row.getInt("metadata");}work.tracked=true;data.pending.add(work);}return data;}
    static void loaded(LevelEvent.Load event){if(event.getLevel()instanceof ServerLevel level)data(level);}
    static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel level){var data=data(level);for(var work:List.copyOf(data.pending))if(data.pending.contains(work)&&data.attempt(level,work)){data.pending.remove(work);data.setDirty();}}}
    static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel level){var data=OPEN.remove(level);if(data!=null)for(var work:data.pending){work.source=null;work.close();}}}
    static void stopped(ServerStoppedEvent event){for(var data:OPEN.values())for(var work:data.pending){work.source=null;work.close();}OPEN.clear();}
}
