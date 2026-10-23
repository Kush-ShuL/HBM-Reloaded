package com.hbm.item.structurelooteditor;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.hazard.HbmHazards;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.loot.*;
import com.mojang.authlib.GameProfile;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** One real marker action. Existing StructureLootMarkers owns replacement/locks/all loot sampling. */
public final class LootEditorPlacement {
    private static final GameProfile PROFILE=new GameProfile(UUID.fromString("839eb18c-50bc-400c-8291-9383f09763e7"),"[NTM]");
    private static final Map<ServerLevel,Set<LootEditorEntity>> WAITING=new WeakHashMap<>();
    static final class Work implements AutoCloseable {
        final NuclearChunkAccess access;final int minX,maxX,minZ,maxZ;
        Work(LootEditorEntity tile){
            var pos=tile.getBlockPos();int left=pos.getX(),right=left,near=pos.getZ(),far=near;
            if(tile.replacement.getBlock() instanceof DummyableBlock machine){
                int yaw=Mth.floor(tile.placedRotation*4F/360F+.5D)&3;var facing=new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST}[yaw];
                var core=pos.above(machine.heightOffset()).relative(facing,-machine.offset());var d=DummyableBlock.rotate(machine.dimensions(),facing);
                left=Math.min(left,core.getX()-d[4]);right=Math.max(right,core.getX()+d[5]);near=Math.min(near,core.getZ()-d[2]);far=Math.max(far,core.getZ()+d[3]);
            }
            minX=left>>4;maxX=right>>4;minZ=near>>4;maxZ=far>>4;
            access=new NuclearChunkAccess(Math.max(9,Math.toIntExact(((long)maxX-minX+3)*((long)maxZ-minZ+3))));
        }
        boolean ready(ServerLevel level){boolean ready=true;for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++)if(access.getWithNeighbors(level,x,z)==null)ready=false;return ready;}
        @Override public void close(){access.close();}
    }
    public static void enlist(LootEditorEntity tile){if(tile.getLevel() instanceof ServerLevel level)WAITING.computeIfAbsent(level,key->Collections.newSetFromMap(new IdentityHashMap<>())).add(tile);}
    static void advance(ServerLevel level,LootEditorEntity tile){
        if(tile.executing||tile.lastAttempt==level.getGameTime())return;tile.lastAttempt=level.getGameTime();
        if(tile.isRemoved()||!tile.trigger){unloaded(tile);return;}
        var marker=tile.settings();
        // Incomplete real source pools remain pending. Never request/pin chunks for fake or missing loot.
        if(!StructureLootMarkers.ready(level,tile.getBlockPos(),tile.replacement,marker)){cancel(tile);return;}
        try{
            if(tile.work==null)tile.work=new Work(tile);if(!tile.work.ready(level))return;
            tile.executing=true;
            try{replace(level,tile,marker);}finally{tile.executing=false;cancel(tile);}
        }catch(RuntimeException|Error failure){cancel(tile);throw failure;}
    }
    private static ItemStack placementStack(BlockState state){
        var stack=new ItemStack(state.getBlock());var properties=new HashMap<String,String>();
        for(var property:state.getProperties())properties.put(property.getName(),value(state,property));
        stack.set(DataComponents.BLOCK_STATE,new BlockItemStateProperties(properties));
        for(var property:state.getProperties())if(property.getName().equals("legacy_meta")&&state.getValue(property) instanceof Integer metadata)stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);
        return stack;
    }
    private static <T extends Comparable<T>> String value(BlockState state,net.minecraft.world.level.block.state.properties.Property<T> property){return property.getName(state.getValue(property));}
    private static void replace(ServerLevel level,LootEditorEntity tile,net.minecraft.nbt.CompoundTag marker){
        var pos=tile.getBlockPos();if(!(level.getBlockState(pos).getBlock() instanceof LootEditorBlock)){
            com.mojang.logging.LogUtils.getLogger().warn("Loot authoring BE lost its real host at {}; cancelling its replacement",pos);return;
        }
        var replacement=tile.replacement;var stack=placementStack(replacement);
        // Source FakePlayer identity/yaw are retained. Native blocks moved their orientation
        // calculation from onBlockPlacedBy into getStateForPlacement; use that actual hook.
        var actor=FakePlayerFactory.get(level,PROFILE);actor.setYRot(tile.placedRotation);actor.setYHeadRot(tile.placedRotation);
        var context=new BlockPlaceContext(actor,InteractionHand.MAIN_HAND,stack,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false)){
            @Override public BlockPos getClickedPos(){return getHitResult().getBlockPos();}
        };
        var oriented=replacement.getBlock().getStateForPlacement(context);if(oriented!=null&&LootEditorEntity.lootable(pos,oriented))replacement=oriented;
        // The sole source marker backend preserves flags2, max-exclusive amount, arbitrary
        // overwrite slots, real lock/cheesable quirks, pile layouts and both original RNG draws.
        StructureLootMarkers.replace(level,pos,replacement,marker,level.random);
        try{replacement.getBlock().setPlacedBy(level,pos,replacement,actor,stack);}
        catch(Exception failure){com.mojang.logging.LogUtils.getLogger().warn("Failed to correctly rotate source loot block at {}",pos,failure);} // Original catches only this final callback.
    }
    public static void cancel(LootEditorEntity tile){if(tile.executing)return;try{if(tile.work!=null)tile.work.close();}finally{tile.work=null;remove(tile);}}
    public static void unloaded(LootEditorEntity tile){if(!tile.executing)cancel(tile);}
    private static void remove(LootEditorEntity tile){if(tile.getLevel() instanceof ServerLevel level){var set=WAITING.get(level);if(set!=null){set.remove(tile);if(set.isEmpty())WAITING.remove(level);}}}
    private static void tick(LevelTickEvent.Post event){
        if(!(event.getLevel() instanceof ServerLevel level))return;var set=WAITING.get(level);if(set==null)return;
        for(var tile:List.copyOf(set)){var pos=tile.getBlockPos();var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);if(chunk==null||tile.isRemoved()||chunk.getBlockEntity(pos)!=tile)unloaded(tile);else advance(level,tile);}
    }
    private static void unloaded(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level){var set=WAITING.remove(level);if(set!=null)for(var tile:set)try{if(tile.work!=null)tile.work.close();}finally{tile.work=null;}}}
    private static void stopped(ServerStoppedEvent event){try{for(var set:WAITING.values())for(var tile:set)try{if(tile.work!=null)tile.work.close();}finally{tile.work=null;}}finally{WAITING.clear();}}
    public static void register(){NeoForge.EVENT_BUS.addListener(LootEditorPlacement::tick);NeoForge.EVENT_BUS.addListener((LevelEvent.Unload event)->unloaded(event));NeoForge.EVENT_BUS.addListener(LootEditorPlacement::stopped);}
    private LootEditorPlacement(){}
}
