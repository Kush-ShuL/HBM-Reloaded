package com.hbm.door.sourceseal;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only BlockSeal's exact frame/search/fill/clear and TileEntityHatch validation callbacks; sole NCA handles cold chunks. */
public final class SourceSealActions extends SavedData {
    private static final byte TOGGLE=0,VALIDATE=1,REDSTONE=2;
    private static final Factory<SourceSealActions> FACTORY=new Factory<>(SourceSealActions::new,SourceSealActions::load);
    private static final Map<ServerLevel,SourceSealActions> OPEN=new WeakHashMap<>();
    private final List<Work> pending=new ArrayList<>();
    private static final class Work {
        final byte kind;
        final BlockPos controller,hatch;
        int meta,phase,size=1,edge,cursor,result=-1;
        boolean valid=true,closed,finished,tracked,executing;
        NuclearChunkAccess access;
        Work(byte kind,BlockPos controller,BlockPos hatch,int meta){this.kind=kind;this.controller=controller.immutable();this.hatch=hatch==null?null:hatch.immutable();this.meta=meta;}
        BlockPos center(){return hatch==null?controller:hatch;}
        void close(){try{if(access!=null)access.close();}finally{access=null;}}
        BlockState read(ServerLevel level,BlockPos pos){
            if(level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null){if(access==null)access=new NuclearChunkAccess();if(access.get(level,pos.getX()>>4,pos.getZ()>>4)==null)return null;}
            return level.getBlockState(pos);
        }
        boolean writeReady(ServerLevel level,BlockPos pos){
            if(haloLoaded(level,pos))return true;
            if(access==null)access=new NuclearChunkAccess();return access.getWithNeighbors(level,pos.getX()>>4,pos.getZ()>>4)!=null;
        }
        int xOffset(){return meta%4==0?size:meta%4==1?-size:0;}
        int zOffset(){return meta%4==2?size:meta%4==3?-size:0;}
        BlockPos border(){int i=-size+cursor;return switch(edge){case 0->controller.offset(i+xOffset(),0,size+zOffset());case 1->controller.offset(i+xOffset(),0,-size+zOffset());case 2->controller.offset(-size+xOffset(),0,i+zOffset());default->controller.offset(size+xOffset(),0,i+zOffset());};}
        BlockPos inside(){int width=2*size-1;return controller.offset(-size+1+cursor/width+xOffset(),0,-size+1+cursor%width+zOffset());}
        void changed(SourceSealActions owner){if(tracked)owner.setDirty();}
        boolean advance(ServerLevel level,SourceSealActions owner){
            if(kind==VALIDATE){
                var current=read(level,hatch);if(current==null)return false;
                // A removed/replaced original BE no longer ticks. Do not let its queued callback erase an unrelated replacement.
                if(!current.is(SourceSealContent.HATCH.get())||!(level.getBlockEntity(hatch)instanceof SourceSealHatchEntity tile)||!tile.controller().equals(controller)){finished=true;return true;}
            }
            while(!finished){
                if(phase==0){
                    if(kind==VALIDATE){var state=read(level,controller);if(state==null)return false;if(!state.is(SourceSealContent.CONTROLLER.get())){phase=4;changed(owner);continue;}meta=state.getValue(SourceSealBlock.META);}
                    if(kind==REDSTONE){
                        if(!writeReady(level,controller))return false;var state=level.getBlockState(controller);if(!state.is(SourceSealContent.CONTROLLER.get())){finished=true;continue;}meta=state.getValue(SourceSealBlock.META);
                        if(level.hasNeighborSignal(controller)){
                            if(meta>=4){finished=true;continue;}
                            level.setBlock(controller,state.setValue(SourceSealBlock.META,meta+4),2);meta+=4;
                        }else{if(meta>=4)level.setBlock(controller,state.setValue(SourceSealBlock.META,meta%4),2);finished=true;continue;}
                    }
                    phase=1;changed(owner);
                }else if(phase==1){
                    if(size>=7){result=0;if(kind==VALIDATE)phase=4;else finished=true;changed(owner);continue;}
                    var state=read(level,border());if(state==null)return false;
                    if(!SourceSealContent.frame(state))valid=false;
                    cursor++;if(cursor>=2*size+1){cursor=0;edge++;}
                    if(edge>=4){if(valid){result=1;if(kind==VALIDATE)finished=true;else phase=2;edge=0;cursor=0;}else{size++;edge=0;cursor=0;valid=true;}}
                    changed(owner);
                }else if(phase==2){
                    int width=2*size-1;if(cursor>=width*width){phase=3;cursor=0;changed(owner);continue;}
                    var state=read(level,inside());if(state==null)return false;
                    if(state.is(SourceSealContent.HATCH.get())){closed=true;phase=3;cursor=0;}else cursor++;
                    changed(owner);
                }else if(phase==3){
                    int width=2*size-1;if(cursor>=width*width){finished=true;continue;}
                    var pos=inside();var state=read(level,pos);if(state==null)return false;
                    boolean edit=closed?state.is(SourceSealContent.HATCH.get()):sourceAir(state);
                    if(edit){if(!writeReady(level,pos))return false;
                        // Re-read the live cell after readiness. No cached palette/block state survives a tick.
                        state=level.getBlockState(pos);edit=closed?state.is(SourceSealContent.HATCH.get()):sourceAir(state);
                        if(edit){level.setBlock(pos,closed?Blocks.AIR.defaultBlockState():SourceSealContent.HATCH.get().defaultBlockState(),3);if(!closed&&level.getBlockEntity(pos)instanceof SourceSealHatchEntity tile)tile.setControllerPos(controller);}
                    }
                    cursor++;changed(owner);
                }else if(phase==4){if(!writeReady(level,hatch))return false;level.setBlock(hatch,Blocks.AIR.defaultBlockState(),3);finished=true;}
            }
            return true;
        }
    }
    private static boolean sourceAir(BlockState state){return state.is(Blocks.AIR)||state.is(Blocks.CAVE_AIR)||state.is(Blocks.VOID_AIR);}
    static boolean haloLoaded(ServerLevel level,BlockPos pos){int x=pos.getX()>>4,z=pos.getZ()>>4;for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)if(level.getChunkSource().getChunkNow(x+dx,z+dz)==null)return false;return true;}
    private static SourceSealActions data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_source_seal_actions"));}
    /** -1 means a genuine cold callback is pending; never claim a valid frame before the source search has completed. */
    public static int toggle(ServerLevel level,BlockPos controller,int meta){var job=new Work(TOGGLE,controller,null,meta);submit(level,job);return job.finished?job.result:-1;}
    public static void redstone(ServerLevel level,BlockPos controller){submit(level,new Work(REDSTONE,controller,null,0));}
    public static void validate(ServerLevel level,BlockPos hatch,BlockPos controller){
        var owner=data(level);for(var job:owner.pending)if(job.kind==VALIDATE&&!job.executing&&job.hatch.equals(hatch)&&job.controller.equals(controller))return;
        owner.submitWork(level,new Work(VALIDATE,controller,hatch,0));
    }
    private static void submit(ServerLevel level,Work job){data(level).submitWork(level,job);}
    private boolean blocked(Work job){for(var older:pending){if(older==job)break;if(older.controller.equals(job.controller)&&older.kind!=VALIDATE)return true;}return false;}
    private void submitWork(ServerLevel level,Work job){
        if(!blocked(job)&&attempt(level,job))return;
        job.tracked=true;pending.add(job);setDirty();
    }
    private boolean attempt(ServerLevel level,Work job){
        if(job.executing)return false;
        var center=job.center();if(level.getChunkSource().getChunkNow(center.getX()>>4,center.getZ()>>4)==null){job.close();return false;}
        try{job.executing=true;boolean complete=job.advance(level,this);if(complete)job.close();return complete;}
        catch(RuntimeException|Error failure){job.close();throw failure;}
        finally{job.executing=false;}
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){
        var list=new ListTag();for(var job:pending){var row=new CompoundTag();row.putByte("kind",job.kind);row.putLong("controller",job.controller.asLong());if(job.hatch!=null)row.putLong("hatch",job.hatch.asLong());row.putInt("meta",job.meta);row.putInt("phase",job.phase);row.putInt("size",job.size);row.putInt("edge",job.edge);row.putInt("cursor",job.cursor);row.putInt("result",job.result);row.putBoolean("valid",job.valid);row.putBoolean("closed",job.closed);list.add(row);}tag.put("pending",list);return tag;
    }
    private static SourceSealActions load(CompoundTag tag,HolderLookup.Provider lookup){
        var data=new SourceSealActions();for(var value:tag.getList("pending",Tag.TAG_COMPOUND)){var row=(CompoundTag)value;var job=new Work(row.getByte("kind"),BlockPos.of(row.getLong("controller")),row.contains("hatch")?BlockPos.of(row.getLong("hatch")):null,row.getInt("meta"));job.phase=row.getInt("phase");job.size=row.getInt("size");job.edge=row.getInt("edge");job.cursor=row.getInt("cursor");job.result=row.getInt("result");job.valid=row.getBoolean("valid");job.closed=row.getBoolean("closed");job.tracked=true;data.pending.add(job);}return data;
    }
    static void loaded(LevelEvent.Load event){if(event.getLevel()instanceof ServerLevel level)data(level);}
    static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel level){var owner=data(level);for(var job:List.copyOf(owner.pending))if(owner.pending.contains(job)&&!owner.blocked(job)&&owner.attempt(level,job)){owner.pending.remove(job);owner.setDirty();}}}
    static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel level){var owner=OPEN.remove(level);if(owner!=null)for(var job:owner.pending)job.close();}}
    static void stopped(ServerStoppedEvent event){for(var owner:OPEN.values())for(var job:owner.pending)job.close();OPEN.clear();}
}
