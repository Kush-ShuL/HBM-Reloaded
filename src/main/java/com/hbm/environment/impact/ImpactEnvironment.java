package com.hbm.environment.impact;

import com.hbm.saveddata.TomSaveData;
import com.hbm.satellite.resources.ImpactTerrainContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** ModEventHandlerImpact + ImpactWorldHandler; direct impact entities and terrain blocks remain Resources113-owned. */
@EventBusSubscriber(modid="hbm")
public final class ImpactEnvironment {
    public static void register(IEventBus bus){ImpactGeneration.register(bus);bus.addListener(ImpactSync::register);}
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void load(LevelEvent.Load event){TomSaveData.resetLastCached();if(event.getLevel() instanceof ServerLevel world)ImpactState.publish(world);}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof Level world){if(world instanceof ServerLevel server)ImpactTerrainWork.forget(server);ImpactState.forget(world);}}
    @SubscribeEvent public static void stop(ServerStoppedEvent event){ImpactTerrainWork.clear();ImpactState.clear();TomSaveData.resetLastCached();}
    @SubscribeEvent public static void loadChunk(ChunkEvent.Load event){if(event.getLevel() instanceof ServerLevel world&&event.getChunk() instanceof LevelChunk chunk)ImpactTerrainWork.loaded(world,chunk);}
    @SubscribeEvent public static void unloadChunk(ChunkEvent.Unload event){if(event.getLevel() instanceof ServerLevel world)ImpactTerrainWork.unloaded(world,event.getChunk().getPos());}
    @SubscribeEvent public static void watch(ChunkWatchEvent.Watch event){ImpactTerrainWork.watched(event.getLevel(),event.getChunk());}
    @SubscribeEvent public static void tick(LevelTickEvent.Pre event){
        if(!(event.getLevel() instanceof ServerLevel world))return;
        var data=TomSaveData.forWorld(world);ImpactTerrainWork.tick(world,new ImpactState(data.fire,data.dust,data.impact));
        float settle=1F/14400000F,cool=1F/24000F;
        if(data.dust>0&&data.fire==0){data.dust=Math.max(0,data.dust-settle);data.setDirty();}
        if(data.fire>0){data.fire=Math.max(0,data.fire-cool);data.dust=Math.min(1,data.dust+cool);data.setDirty();}
        ImpactState.publish(world);
        if(world.dimension()==Level.OVERWORLD&&data.fire>0&&data.dust<.75F){
            var entities=new ArrayList<Entity>();world.getAllEntities().forEach(entities::add);
            for(var entity:entities)if(entity instanceof LivingEntity&&world.getBrightness(LightLayer.SKY,new BlockPos((int)entity.getX(),(int)entity.getY(),(int)entity.getZ()))>7){entity.igniteForSeconds(5);entity.hurt(world.damageSources().onFire(),2F);}
        }
    }
    @SubscribeEvent public static void publish(LevelTickEvent.Post event){if(event.getLevel() instanceof ServerLevel world)ImpactState.publish(world);}
    @SubscribeEvent public static void sync(PlayerTickEvent.Pre event){if(event.getEntity() instanceof ServerPlayer player)ImpactSync.send(player,ImpactState.get(player.serverLevel()));}
    static void die(ServerLevel world,BlockPos pos,float dust){
        var above=pos.above();int light=Math.max(world.getBrightness(LightLayer.BLOCK,above),(int)(world.getMaxLocalRawBrightness(above)*(1-dust)));if(light>=4)return;
        var state=world.getBlockState(pos);if(state.is(Blocks.GRASS_BLOCK))world.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);
        else if(state.getBlock() instanceof BushBlock||state.is(BlockTags.LEAVES)||state.getBlock() instanceof VineBlock)world.setBlock(pos,Blocks.AIR.defaultBlockState(),3);
    }
    static void burn(ServerLevel world,BlockPos pos){
        var state=world.getBlockState(pos);int sky=world.getBrightness(LightLayer.SKY,pos.above());
        if(state.isFlammable(world,pos,Direction.UP)&&world.getBlockState(pos.above()).is(Blocks.AIR)&&sky>=7){
            if(state.is(BlockTags.LEAVES)||state.getBlock() instanceof BushBlock)world.removeBlock(pos,false);world.setBlock(pos.above(),Blocks.FIRE.defaultBlockState(),3);
        }else if((state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.MYCELIUM)||named(state,"waste_earth")||named(state,"frozen_grass")||named(state,"waste_mycelium"))&&!world.isRainingAt(pos)&&sky>=7)world.setBlock(pos,ImpactTerrainContent.BURNING.get().defaultBlockState(),3);
        else if(named(state,"frozen_dirt")&&sky>=7)world.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);
    }
    private static boolean named(net.minecraft.world.level.block.state.BlockState state,String name){return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm",name));}
    @SubscribeEvent public static void extinction(MobSpawnEvent.PositionCheck event){
        var world=event.getLevel().getLevel();if(!(world.getServer().isSameThread()?ImpactState.get(world):ImpactState.generation(world)).impact())return;var mob=event.getEntity();
        // Keep the original precedence: tall animals (including babies/water mobs) hit the first arm.
        boolean deny=world.dimension()==Level.OVERWORLD&&(mob.getBbHeight()>=.85F||mob.getBbWidth()>=.85F&&!(mob instanceof WaterAnimal)&&!mob.isBaby());
        if(mob instanceof WaterAnimal&&new Random().nextInt(5)!=0)deny=true;
        if(deny){event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);mob.discard();}
    }
    private ImpactEnvironment(){}
}
