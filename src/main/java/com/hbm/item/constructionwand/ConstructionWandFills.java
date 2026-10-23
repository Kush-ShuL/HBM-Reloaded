package com.hbm.item.constructionwand;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.world.structure.templates.TemplateStateMappings;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only ItemWand's original inclusive i->j->k fill, suspended at the exact cold cell via the sole NCA backend. */
public final class ConstructionWandFills extends SavedData {
    private static final Factory<ConstructionWandFills> FACTORY=new Factory<>(ConstructionWandFills::new,ConstructionWandFills::load);
    private static final Map<ServerLevel,ConstructionWandFills> OPEN=new WeakHashMap<>();
    private final ArrayDeque<Work> pending=new ArrayDeque<>();
    private boolean executing;
    private static final class Work {
        final BlockPos minimum,maximum;
        final BlockState selected;
        final UUID player;
        final boolean airOnly;
        int x,y,z;
        boolean finished;
        NuclearChunkAccess access;
        Work(BlockPos a,BlockPos b,BlockState state,UUID player){
            minimum=new BlockPos(Math.min(a.getX(),b.getX()),Math.min(a.getY(),b.getY()),Math.min(a.getZ(),b.getZ()));
            maximum=new BlockPos(Math.max(a.getX(),b.getX()),Math.max(a.getY(),b.getY()),Math.max(a.getZ(),b.getZ()));
            selected=state;this.player=player;airOnly=state.is(ConstructionWandContent.AIR_MARKER.get());x=minimum.getX();y=minimum.getY();z=minimum.getZ();
        }
        void advance(){if(z!=maximum.getZ()){z++;return;}if(y!=maximum.getY()){z=minimum.getZ();y++;return;}if(x!=maximum.getX()){z=minimum.getZ();y=minimum.getY();x++;return;}finished=true;}
        void close(){try{if(access!=null)access.close();}finally{access=null;}}
        boolean step(ServerLevel level,ConstructionWandFills owner){
            int preparedX=Integer.MIN_VALUE,preparedZ=Integer.MIN_VALUE;
            while(!finished){
                var pos=new BlockPos(x,y,z);
                // The native setBlock rejects these cells before querying a chunk; retain their place in the source traversal.
                if(!level.isInWorldBounds(pos)){advance();owner.setDirty();continue;}
                int chunkX=x>>4,chunkZ=z>>4;
                if(preparedX!=chunkX||preparedZ!=chunkZ){
                    if(access==null)access=new NuclearChunkAccess();
                    if(access.getWithNeighbors(level,chunkX,chunkZ)==null)return false;
                    preparedX=chunkX;preparedZ=chunkZ;
                }
                // No live world read, shape update or flags3 mutation happens before the real target+halo is FULL.
                var current=level.getBlockState(pos);
                boolean sourceAir=current.is(Blocks.AIR)||current.is(Blocks.CAVE_AIR)||current.is(Blocks.VOID_AIR);
                if(!airOnly||sourceAir)level.setBlock(pos,Block.updateFromNeighbourShapes(selected,level,pos),Block.UPDATE_ALL);
                advance();owner.setDirty();
            }
            return true;
        }
    }
    private static ConstructionWandFills data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_construction_wand_fills"));}
    public static void submit(ServerLevel level,BlockPos first,BlockPos second,BlockState state,UUID player){
        var owner=data(level);owner.pending.add(new Work(first,second,state,player));owner.setDirty();owner.attempt(level);
    }
    private void attempt(ServerLevel level){
        if(executing)return;
        executing=true;
        try{
            while(!pending.isEmpty()){
                var job=pending.peek();
                try{if(!job.step(level,this))return;}
                catch(RuntimeException|Error failure){job.close();throw failure;}
                job.close();pending.remove();setDirty();
                var player=level.getServer().getPlayerList().getPlayer(job.player);
                if(player!=null)player.displayClientMessage(Component.translatable("message.hbm.construction_wand.filled"),false);
            }
        }finally{executing=false;}
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){
        var list=new ListTag();for(var job:pending){
            var row=new CompoundTag();row.putInt("minX",job.minimum.getX());row.putInt("minY",job.minimum.getY());row.putInt("minZ",job.minimum.getZ());row.putInt("maxX",job.maximum.getX());row.putInt("maxY",job.maximum.getY());row.putInt("maxZ",job.maximum.getZ());row.putInt("x",job.x);row.putInt("y",job.y);row.putInt("z",job.z);
            row.put("state",NbtUtils.writeBlockState(job.selected));row.putUUID("player",job.player);row.putBoolean("finished",job.finished);list.add(row);
        }tag.put("pending",list);return tag;
    }
    private static ConstructionWandFills load(CompoundTag tag,HolderLookup.Provider lookup){
        var owner=new ConstructionWandFills();for(var value:tag.getList("pending",Tag.TAG_COMPOUND)){
            var row=(CompoundTag)value;var job=new Work(new BlockPos(row.getInt("minX"),row.getInt("minY"),row.getInt("minZ")),new BlockPos(row.getInt("maxX"),row.getInt("maxY"),row.getInt("maxZ")),TemplateStateMappings.parse(row.getCompound("state")),row.getUUID("player"));
            job.x=row.getInt("x");job.y=row.getInt("y");job.z=row.getInt("z");job.finished=row.getBoolean("finished");owner.pending.add(job);
        }return owner;
    }
    private void close(){for(var job:pending)job.close();}
    static void loaded(LevelEvent.Load event){if(event.getLevel()instanceof ServerLevel level)data(level);}
    static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel level)data(level).attempt(level);}
    static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel level){var owner=OPEN.remove(level);if(owner!=null)owner.close();}}
    static void stopped(ServerStoppedEvent event){for(var owner:OPEN.values())owner.close();OPEN.clear();}
}
