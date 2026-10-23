package com.hbm.world.structure.tandem;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.config.SourceServerConfig;
import com.hbm.item.structuretandem.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** A bounded current-world waiting action. Native pool/template placement remains the ONLY builder. */
public final class TandemPlacement {
    private static final Map<ServerLevel,Set<TandemEntity>> WAITING=new WeakHashMap<>();
    public static final class Work implements AutoCloseable {
        final StructurePoolElement element;final BlockPos origin;final Rotation rotation;
        final NuclearChunkAccess access;final int minX,maxX,minZ,maxZ;
        Work(ServerLevel level,TandemEntity tile,CompoundTag saved){
            element=StructurePoolElement.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE,level.registryAccess()),saved.get("element")).getOrThrow();
            origin=BlockPos.of(saved.getLong("origin"));rotation=Rotation.valueOf(saved.getString("rotation"));
            var bounds=element.getBoundingBox(level.getStructureManager(),origin,rotation);var center=tile.getBlockPos();
            minX=Math.min(bounds.minX()>>4,center.getX()>>4);maxX=Math.max(bounds.maxX()>>4,center.getX()>>4);
            minZ=Math.min(bounds.minZ()>>4,center.getZ()>>4);maxZ=Math.max(bounds.maxZ()>>4,center.getZ()>>4);
            // Keep precisely this piece's FULL union plus one-chunk halo, so no earlier ready part can be evicted.
            int resident=Math.toIntExact(((long)maxX-minX+3)*((long)maxZ-minZ+3));access=new NuclearChunkAccess(Math.max(9,resident));
        }
        boolean ready(ServerLevel level){
            boolean ready=true;
            for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++)if(access.getWithNeighbors(level,x,z)==null)ready=false;
            return ready;
        }
        void build(ServerLevel level,TandemEntity tile){
            var footprint=element.getBoundingBox(level.getStructureManager(),origin,rotation);
            // Native chunk callbacks use a full-height clip. Source spike conform changes final Y,
            // so do not clip its processed cells back into the un-conformed template height.
            var bounds=new net.minecraft.world.level.levelgen.structure.BoundingBox(footprint.minX(),level.getMinBuildHeight(),footprint.minZ(),footprint.maxX(),level.getMaxBuildHeight()-1,footprint.maxZ());
            try(var ignored=TandemSourceScope.using(tile.condition,level.random)){
                // Source builds ONE selected piece, not all native Jigsaw children/fallbacks.
                // Actual native element settings/processors retain source selectors/loot/spike/connections.
                element.place(level.getStructureManager(),level,level.structureManager(),level.getChunkSource().getGenerator(),origin,origin,rotation,bounds,level.random,LiquidSettings.IGNORE_WATERLOGGING,SourceServerConfig.STRUCTURE_DEBUG.get());
            }
            level.setBlock(tile.getBlockPos(),tile.replacement,2);
        }
        @Override public void close(){access.close();}
    }

    public static void advance(ServerLevel level,TandemEntity tile){
        if(tile.executing||tile.lastAttempt==level.getGameTime())return;tile.lastAttempt=level.getGameTime();
        if(tile.isRemoved()||!tile.armed||tile.pool.isEmpty()||tile.target.isEmpty()){cancel(tile);return;}
        if(tile.pending==null){
            var selected=TandemSourcePools.select(level,tile.getBlockPos(),tile.getBlockState().getValue(TandemBlock.FACING),tile.condition,tile.pool,tile.target);
            if(selected==null)return;
            var saved=new CompoundTag();saved.put("element",StructurePoolElement.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE,level.registryAccess()),selected.element()).getOrThrow());
            saved.putLong("origin",selected.origin().asLong());saved.putString("rotation",selected.rotation().name());tile.pending=saved;tile.setChanged();
        }
        WAITING.computeIfAbsent(level,key->Collections.newSetFromMap(new IdentityHashMap<>())).add(tile);
        try{
            if(tile.work==null)tile.work=new Work(level,tile,tile.pending);
            if(!tile.work.ready(level))return;
            tile.executing=true;
            try{tile.work.build(level,tile);tile.pending=null;tile.setChanged();}
            finally{tile.executing=false;release(tile);removeWaiting(tile);}
        }catch(RuntimeException|Error failure){release(tile);removeWaiting(tile);throw failure;}
    }
    private static void release(TandemEntity tile){try{if(tile.work!=null)tile.work.close();}finally{tile.work=null;}}
    private static void removeWaiting(TandemEntity tile){if(tile.getLevel() instanceof ServerLevel level){var values=WAITING.get(level);if(values!=null){values.remove(tile);if(values.isEmpty())WAITING.remove(level);}}}
    public static void cancel(TandemEntity tile){if(tile.executing)return;release(tile);removeWaiting(tile);tile.pending=null;}
    /** Native removal can occur inside the real build; do not release its halo until the build finally exits. */
    public static void unloaded(TandemEntity tile){if(!tile.executing){release(tile);removeWaiting(tile);}}
    private static void tick(LevelTickEvent.Post event){
        if(!(event.getLevel() instanceof ServerLevel level))return;var values=WAITING.get(level);if(values==null)return;
        for(var tile:List.copyOf(values)){
            var pos=tile.getBlockPos();var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
            if(chunk==null||tile.isRemoved()||chunk.getBlockEntity(pos)!=tile){unloaded(tile);continue;}
            advance(level,tile);
        }
    }
    private static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level){var values=WAITING.remove(level);if(values!=null)for(var tile:values)release(tile);}}
    private static void stop(ServerStoppedEvent event){try{for(var values:WAITING.values())for(var tile:values)release(tile);}finally{WAITING.clear();}}
    public static void register(){NeoForge.EVENT_BUS.addListener(TandemPlacement::tick);NeoForge.EVENT_BUS.addListener(TandemPlacement::unload);NeoForge.EVENT_BUS.addListener(TandemPlacement::stop);}
    private TandemPlacement(){}
}
