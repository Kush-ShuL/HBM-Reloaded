package com.hbm.world.structure;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.config.SourceServerConfig;
import com.hbm.item.structurewand.*;
import java.io.IOException;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only the three real authoring actions. Cold continuation retains source cell order; the sole FULL/halo backend owns tickets. */
public final class StructureAuthoringJobs extends SavedData {
    private static final Factory<StructureAuthoringJobs> FACTORY=new Factory<>(StructureAuthoringJobs::new,StructureAuthoringJobs::read);
    private static final Map<ServerLevel,StructureAuthoringJobs> OPEN=new WeakHashMap<>();
    private final ArrayDeque<Work> pending=new ArrayDeque<>();private boolean executing;
    private enum Action { CONFIGURE,SAVE,LOAD }
    private static final class Work {
        final Action action;final UUID player;final BlockPos controller;final int sizeX,sizeY,sizeZ;final String name;final Set<StructureBlacklist.Key> blacklist;final boolean debug;
        int x,y,z;boolean failed;String failure="";CompoundTag template;NuclearChunkAccess access;
        Map<BlockState,Integer> palette;Map<Long,List<CompoundTag>> columns;
        Work(Action action,UUID player,BlockPos controller,int sizeX,int sizeY,int sizeZ,String name,Set<StructureBlacklist.Key> blacklist,boolean debug,CompoundTag template){this.action=action;this.player=player;this.controller=controller.immutable();this.sizeX=sizeX;this.sizeY=sizeY;this.sizeZ=sizeZ;this.name=name;this.blacklist=Set.copyOf(blacklist);this.debug=debug;this.template=template;}
        void close(){try{if(access!=null)access.close();}finally{access=null;}}
        boolean ready(ServerLevel level,BlockPos position){if(access==null)access=new NuclearChunkAccess();return access.getWithNeighbors(level,position.getX()>>4,position.getZ()>>4)!=null;}
        boolean configure(ServerLevel level){
            if(!level.isInWorldBounds(controller)){message(level,player,"message.hbm.structure_wand.failed_controller",true);return true;}
            if(!ready(level,controller))return false;
            level.setBlock(controller,StructureWandContent.SAVE_BLOCK.get().defaultBlockState(),3);
            if(!(level.getBlockEntity(controller) instanceof StructureAuthoringEntity tile)){message(level,player,"message.hbm.structure_wand.failed_controller",true);return true;}
            tile.sizeX=sizeX;tile.sizeY=sizeY;tile.sizeZ=sizeZ;tile.blacklist=new HashSet<>(blacklist);tile.setChanged();level.sendBlockUpdated(controller,tile.getBlockState(),tile.getBlockState(),2);
            message(level,player,"message.hbm.structure_wand.configured",false,controller.getX(),controller.getY(),controller.getZ());return true;
        }
        boolean capture(ServerLevel level,StructureAuthoringJobs owner,long deadline)throws IOException{
            if(palette==null)palette=StructureAuthoringFiles.palette(template);
            // Source saveArea order is x,y,z, distinct from its later x,z,y placement order.
            int preparedX=Integer.MIN_VALUE,preparedZ=Integer.MIN_VALUE,processed=0;
            while(x<sizeX){var pos=controller.offset(x,y+1,z);
                if(level.isInWorldBounds(pos)){
                    int cx=pos.getX()>>4,cz=pos.getZ()>>4;if(cx!=preparedX||cz!=preparedZ){if(!ready(level,pos))return false;preparedX=cx;preparedZ=cz;}
                    StructureAuthoringFiles.capture(level,pos,x,y,z,blacklist,template,palette);
                }
                if(++z==sizeZ){z=0;if(++y==sizeY){y=0;x++;}}owner.setDirty();
                if(++processed%1024==0&&System.nanoTime()>=deadline)return false;
            }
            var path=StructureAuthoringFiles.write(name,template);var actor=level.getServer().getPlayerList().getPlayer(player);
            if(actor!=null){var link=Component.literal(path.getFileName().toString()).withStyle(style->style.withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE,path.getParent().toAbsolutePath().toString())));actor.sendSystemMessage(Component.translatable("message.hbm.structure_wand.saved",link));}
            return true;
        }
        boolean place(ServerLevel level,StructureAuthoringJobs owner,long deadline){
            if(columns==null)columns=StructureAuthoringFiles.columns(template);var origin=controller.above();
            int preparedX=Integer.MIN_VALUE,preparedZ=Integer.MIN_VALUE;
            while(x<sizeX){var base=origin.offset(x,0,z);int cx=base.getX()>>4,cz=base.getZ()>>4;
                if(cx!=preparedX||cz!=preparedZ){if(!ready(level,base))return false;preparedX=cx;preparedZ=cz;}
                var column=StructureAuthoringFiles.column(level,template,columns.get(ChunkPos.asLong(x,z)),x,z,origin,debug);
                var settings=new StructurePlaceSettings().setRotation(net.minecraft.world.level.block.Rotation.NONE).setKnownShape(true).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING).setIgnoreEntities(true);
                NativeTemplateStructures.sourceProcessors(settings);
                var nativeHousekeeping=settings.getRandom(origin);settings.setRandom(level.random);
                boolean previous=SourceServerConfig.STRUCTURE_DEBUG.get();SourceServerConfig.STRUCTURE_DEBUG.set(debug);
                try(var source=com.hbm.world.structure.tandem.TandemSourceScope.using(null,level.random)){column.placeInWorld(level,origin,origin,settings,nativeHousekeeping,2);}finally{SourceServerConfig.STRUCTURE_DEBUG.set(previous);}
                // Only after the entire native column (processors, BEs and writes) completed may the persisted source cursor advance.
                if(++z==sizeZ){z=0;x++;}owner.setDirty();
                if(System.nanoTime()>=deadline)return false;
            }
            if(!ready(level,controller))return false;
            if(level.getBlockEntity(controller) instanceof StructureAuthoringEntity tile){
                var settings=tile.settings();settings.putInt("sizeX",sizeX);settings.putInt("sizeY",sizeY);settings.putInt("sizeZ",sizeZ);
                level.setBlock(controller,StructureWandContent.SAVE_BLOCK.get().defaultBlockState(),3);
                if(level.getBlockEntity(controller) instanceof StructureAuthoringEntity saved){saved.readSettings(settings);saved.setChanged();level.sendBlockUpdated(controller,saved.getBlockState(),saved.getBlockState(),2);}
            }
            message(level,player,"message.hbm.structure_wand.loaded",false);return true;
        }
        boolean step(ServerLevel level,StructureAuthoringJobs owner,long deadline)throws IOException{return switch(action){case CONFIGURE->configure(level);case SAVE->capture(level,owner,deadline);case LOAD->place(level,owner,deadline);};}
        CompoundTag save(){var tag=new CompoundTag();tag.putString("action",action.name());tag.putUUID("player",player);tag.putLong("controller",controller.asLong());tag.putInt("sizeX",sizeX);tag.putInt("sizeY",sizeY);tag.putInt("sizeZ",sizeZ);tag.putString("name",name);tag.putBoolean("debug",debug);tag.putInt("x",x);tag.putInt("y",y);tag.putInt("z",z);tag.putBoolean("failed",failed);tag.putString("failure",failure);StructureBlacklist.write(tag,blacklist);if(template!=null)tag.put("template",template.copy());return tag;}
        static Work read(CompoundTag tag){var work=new Work(Action.valueOf(tag.getString("action")),tag.getUUID("player"),BlockPos.of(tag.getLong("controller")),tag.getInt("sizeX"),tag.getInt("sizeY"),tag.getInt("sizeZ"),tag.getString("name"),StructureBlacklist.read(tag),tag.getBoolean("debug"),tag.contains("template")?tag.getCompound("template").copy():null);work.x=tag.getInt("x");work.y=tag.getInt("y");work.z=tag.getInt("z");work.failed=tag.getBoolean("failed");work.failure=tag.getString("failure");return work;}
    }
    private static StructureAuthoringJobs data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_structure_authoring_jobs"));}
    private static void submit(ServerLevel level,Work work){var owner=data(level);owner.pending.add(work);owner.setDirty();owner.attempt(level);}
    public static void configure(ServerLevel level,UUID player,BlockPos controller,int x,int y,int z,Set<StructureBlacklist.Key> blacklist){submit(level,new Work(Action.CONFIGURE,player,controller,x,y,z,"",blacklist,false,null));}
    public static void save(ServerLevel level,UUID player,StructureAuthoringEntity tile){
        if(tile.name.isEmpty()){message(level,player,"message.hbm.structure_wand.invalid_save_name",true);return;}
        if(tile.sizeX<=0||tile.sizeY<=0||tile.sizeZ<=0){message(level,player,"message.hbm.structure_wand.invalid_size",true);return;}
        submit(level,new Work(Action.SAVE,player,tile.getBlockPos(),tile.sizeX,tile.sizeY,tile.sizeZ,tile.name,tile.blacklist,false,StructureAuthoringFiles.blank(tile.sizeX,tile.sizeY,tile.sizeZ)));
    }
    public static void load(ServerLevel level,UUID player,StructureAuthoringEntity tile){
        if(tile.name.isEmpty()){message(level,player,"message.hbm.structure_wand.invalid_load_name",true);return;}
        try{var template=StructureAuthoringFiles.read(tile.name);var size=template.getList("size",Tag.TAG_INT);tile.sizeX=size.getInt(0);tile.sizeY=size.getInt(1);tile.sizeZ=size.getInt(2);tile.setChanged();submit(level,new Work(Action.LOAD,player,tile.getBlockPos(),tile.sizeX,tile.sizeY,tile.sizeZ,tile.name,tile.blacklist,!level.hasNeighborSignal(tile.getBlockPos()),template));}
        catch(java.nio.file.NoSuchFileException error){message(level,player,"message.hbm.structure_wand.file_missing",true);}
        catch(IOException|IllegalArgumentException error){message(level,player,"message.hbm.structure_wand.load_error",true,error.getMessage());}
    }
    private static void message(ServerLevel level,UUID player,String key,boolean error,Object... args){var actor=level.getServer().getPlayerList().getPlayer(player);if(actor!=null)actor.sendSystemMessage(error?Component.translatable(key,args).withStyle(ChatFormatting.RED):Component.translatable(key,args));}
    private void attempt(ServerLevel level){if(executing)return;executing=true;long deadline=System.nanoTime()+8_000_000L;
        try{for(var iterator=pending.iterator();iterator.hasNext();){
            var job=iterator.next();if(job.failed)continue;
            try{if(!job.step(level,this,deadline))return;job.close();iterator.remove();setDirty();}
            catch(IOException|RuntimeException failure){
                // Keep the failed cursor/template; a partly placed column is never marked completed or retried every tick.
                job.failed=true;job.failure=failure.toString();job.close();setDirty();
                com.mojang.logging.LogUtils.getLogger().error("Structure authoring {} failed at {} [{},{},{}]",job.action,job.controller,job.x,job.y,job.z,failure);
                message(level,job.player,job.action==Action.SAVE?"message.hbm.structure_wand.save_error":"message.hbm.structure_wand.load_error",true,failure.getMessage());
            }
            if(System.nanoTime()>=deadline)return;
        }}finally{executing=false;}}

    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){var list=new ListTag();for(var job:pending)list.add(job.save());tag.put("pending",list);return tag;}
    private static StructureAuthoringJobs read(CompoundTag tag,HolderLookup.Provider lookup){var result=new StructureAuthoringJobs();for(var raw:tag.getList("pending",Tag.TAG_COMPOUND))result.pending.add(Work.read((CompoundTag)raw));return result;}
    private void close(){for(var job:pending)job.close();}
    private static void loaded(LevelEvent.Load event){if(event.getLevel() instanceof ServerLevel level)data(level);}
    private static void tick(LevelTickEvent.Post event){if(event.getLevel() instanceof ServerLevel level)data(level).attempt(level);}
    private static void unloaded(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level){var owner=OPEN.remove(level);if(owner!=null)owner.close();}}
    private static void stopped(ServerStoppedEvent event){for(var owner:OPEN.values())owner.close();OPEN.clear();}
    public static void register(){NeoForge.EVENT_BUS.addListener(StructureAuthoringJobs::loaded);NeoForge.EVENT_BUS.addListener(StructureAuthoringJobs::tick);NeoForge.EVENT_BUS.addListener(StructureAuthoringJobs::unloaded);NeoForge.EVENT_BUS.addListener(StructureAuthoringJobs::stopped);}
}
