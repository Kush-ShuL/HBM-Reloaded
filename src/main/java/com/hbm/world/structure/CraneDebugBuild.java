package com.hbm.world.structure;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only the original manual crane.build action; native ray/template calls wait for the sole asynchronous FULL backend. */
public final class CraneDebugBuild extends SavedData {
    private static final Factory<CraneDebugBuild> FACTORY=new Factory<>(CraneDebugBuild::new,CraneDebugBuild::load);
    private static final Map<ServerLevel,CraneDebugBuild> OPEN=new WeakHashMap<>();
    private final ArrayDeque<Work> pending=new ArrayDeque<>();
    private boolean executing;
    private static final class Work {
        final UUID player;
        final Vec3 from,to;
        int phase;
        BlockPos origin;
        List<Long> rayChunks;
        NuclearChunkAccess access;
        Work(UUID player,Vec3 from,Vec3 to){this.player=player;this.from=from;this.to=to;}
        void close(){try{if(access!=null)access.close();}finally{access=null;}}
        boolean step(ServerLevel level,CraneDebugBuild owner){
            if(phase==0){
                var actor=level.getServer().getPlayerList().getPlayer(player);
                if(actor==null||actor.serverLevel()!=level){close();return false;} // Keep the real entity collision context; no synthetic/fake player.
                if(rayChunks==null){
                    var chunks=new LinkedHashSet<Long>();
                    // The same native traversal as Level.clip, used only for pure coordinate discovery; no block reads or second ray engine.
                    BlockGetter.traverseBlocks(from,to,chunks,(keys,pos)->{if(level.isInWorldBounds(pos))keys.add(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));return null;},keys->null);
                    rayChunks=List.copyOf(chunks);
                }
                if(access==null)access=new NuclearChunkAccess();boolean ready=true;
                for(long key:rayChunks)if(access.get(level,ChunkPos.getX(key),ChunkPos.getZ(key))==null)ready=false;
                if(!ready)return false;
                var hit=level.clip(new ClipContext(from,to,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,actor));
                if(hit.getType()!=HitResult.Type.BLOCK)return true;
                var target=hit.getBlockPos();int y=level.getHeight(Heightmap.Types.WORLD_SURFACE,target.getX(),target.getZ())-1;
                var cursor=new BlockPos.MutableBlockPos(target.getX(),y,target.getZ());
                while(y>=level.getMinBuildHeight()){cursor.setY(y);if(level.getBlockState(cursor).getLightBlock(level,cursor)>0)break;y--;}
                var template=actualTemplate(level);var size=template.getSize();origin=new BlockPos(target.getX()-size.getX()/2,y+1,target.getZ()-size.getZ()/2);phase=1;owner.setDirty();
            }
            var template=actualTemplate(level);var size=template.getSize();
            if(access==null)access=new NuclearChunkAccess();boolean ready=true;
            int minX=origin.getX()>>4,maxX=(origin.getX()+size.getX()-1)>>4,minZ=origin.getZ()>>4,maxZ=(origin.getZ()+size.getZ()-1)>>4;
            // The fixed source crane is52x69x17: at most28 unique halo chunks, within existing resident64/pending16 limits.
            if((long)(maxX-minX+3)*(maxZ-minZ+3)>64)throw new IllegalStateException("Actual crane exceeds the existing native work residency; source module requires a coordinated backend contract");
            for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++)if(access.getWithNeighbors(level,x,z)==null)ready=false;
            if(!ready)return false;
            var settings=new StructurePlaceSettings().setRotation(Rotation.NONE).setKnownShape(false).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING).setIgnoreEntities(false).setFinalizeEntities(true);
            NativeTemplateStructures.sourceProcessors(settings);
            // Original manual overload: center=true, rotation0, wipe=false, suppliedY; no natural heightOffset/weight/dimension gates.
            template.placeInWorld(level,origin,origin,settings,level.random,2);return true;
        }
    }
    private static net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate actualTemplate(ServerLevel level){
        var source=NativeTemplateStructures.definition("crane");if(!source.available())throw new IllegalStateException("Actual source crane dependencies are unavailable: "+source.pending());
        return NativeTemplateStructures.template(level.getStructureManager(),ResourceLocation.parse(source.template()));
    }
    private static CraneDebugBuild data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_debug_crane_builds"));}
    public static void submit(ServerLevel level,UUID player,Vec3 from,Vec3 to){var owner=data(level);owner.pending.add(new Work(player,from,to));owner.setDirty();owner.attempt(level);}
    private void attempt(ServerLevel level){
        if(executing)return;executing=true;
        try{while(!pending.isEmpty()){
            var job=pending.peek();try{if(!job.step(level,this))return;}catch(RuntimeException|Error failure){job.close();throw failure;}
            job.close();pending.remove();setDirty();
        }}finally{executing=false;}
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){
        var list=new ListTag();for(var job:pending){var row=new CompoundTag();row.putUUID("player",job.player);row.putDouble("fromX",job.from.x);row.putDouble("fromY",job.from.y);row.putDouble("fromZ",job.from.z);row.putDouble("toX",job.to.x);row.putDouble("toY",job.to.y);row.putDouble("toZ",job.to.z);row.putInt("phase",job.phase);if(job.origin!=null){row.putInt("x",job.origin.getX());row.putInt("y",job.origin.getY());row.putInt("z",job.origin.getZ());}list.add(row);}tag.put("pending",list);return tag;
    }
    private static CraneDebugBuild load(CompoundTag tag,HolderLookup.Provider lookup){
        var owner=new CraneDebugBuild();for(var value:tag.getList("pending",Tag.TAG_COMPOUND)){var row=(CompoundTag)value;var job=new Work(row.getUUID("player"),new Vec3(row.getDouble("fromX"),row.getDouble("fromY"),row.getDouble("fromZ")),new Vec3(row.getDouble("toX"),row.getDouble("toY"),row.getDouble("toZ")));job.phase=row.getInt("phase");if(row.contains("x"))job.origin=new BlockPos(row.getInt("x"),row.getInt("y"),row.getInt("z"));owner.pending.add(job);}return owner;
    }
    private void close(){for(var job:pending)job.close();}
    private static void loaded(LevelEvent.Load event){if(event.getLevel()instanceof ServerLevel level)data(level);}
    private static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel level)data(level).attempt(level);}
    private static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel level){var owner=OPEN.remove(level);if(owner!=null)owner.close();}}
    private static void stopped(ServerStoppedEvent event){for(var owner:OPEN.values())owner.close();OPEN.clear();}
    public static void register(){NeoForge.EVENT_BUS.addListener(CraneDebugBuild::loaded);NeoForge.EVENT_BUS.addListener(CraneDebugBuild::tick);NeoForge.EVENT_BUS.addListener(CraneDebugBuild::unload);NeoForge.EVENT_BUS.addListener(CraneDebugBuild::stopped);}
}
