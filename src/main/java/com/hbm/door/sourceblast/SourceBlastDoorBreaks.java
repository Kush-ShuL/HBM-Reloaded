package com.hbm.door.sourceblast;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only an old dummy's original destroy-parent tail when that explicit absolute target/flags3 halo is cold. */
public final class SourceBlastDoorBreaks extends SavedData {
    private static final Factory<SourceBlastDoorBreaks> FACTORY=new Factory<>(SourceBlastDoorBreaks::new,SourceBlastDoorBreaks::load);
    private static final Map<ServerLevel,SourceBlastDoorBreaks> OPEN=new WeakHashMap<>();
    private final ArrayDeque<BlockPos> pending=new ArrayDeque<>();
    private NuclearChunkAccess access;
    private boolean executing;
    private static SourceBlastDoorBreaks data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_source_blast_door_breaks"));}
    static void submit(ServerLevel level,BlockPos pos){var data=data(level);data.pending.add(pos.immutable());data.setDirty();data.attempt(level);}
    private void close(){try{if(access!=null)access.close();}finally{access=null;}}
    private void attempt(ServerLevel level){
        if(executing||pending.isEmpty())return;
        try{if(access==null)access=new NuclearChunkAccess();var pos=pending.peek();if(access.getWithNeighbors(level,pos.getX()>>4,pos.getZ()>>4)==null)return;
            executing=true;try{level.destroyBlock(pos,true);pending.remove();setDirty();}finally{executing=false;close();}
        }catch(RuntimeException|Error failure){close();throw failure;}
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){var list=new ListTag();for(var pos:pending){var row=new CompoundTag();row.putLong("parent",pos.asLong());list.add(row);}tag.put("pending",list);return tag;}
    private static SourceBlastDoorBreaks load(CompoundTag tag,HolderLookup.Provider lookup){var data=new SourceBlastDoorBreaks();for(var value:tag.getList("pending",Tag.TAG_COMPOUND))data.pending.add(BlockPos.of(((CompoundTag)value).getLong("parent")));return data;}
    static void loaded(LevelEvent.Load event){if(event.getLevel()instanceof ServerLevel server)data(server);}
    static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel server)data(server).attempt(server);}
    static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel server){var data=OPEN.remove(server);if(data!=null)data.close();}}
    static void stopped(ServerStoppedEvent event){for(var data:OPEN.values())data.close();OPEN.clear();}
}
