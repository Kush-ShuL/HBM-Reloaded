package com.hbm.entity.mob.radbeast;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.entity.mob.fbi.FbiConfig;
import com.hbm.machine.reactor.ReactorConfig;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Only BossSpawnHandler's actual elemental branch. Existing reactor marks/config and radiation remain sole owners. */
public final class ElementalEncounters {
    private static final Map<ServerLevel,List<Encounter>> PENDING=new IdentityHashMap<>();
    public static void register(){NeoForge.EVENT_BUS.addListener(ElementalEncounters::unload);NeoForge.EVENT_BUS.addListener(ElementalEncounters::stop);}
    public static void update(ServerLevel world){
        var pending=PENDING.get(world);
        if(pending!=null){for(var iterator=pending.iterator();iterator.hasNext();){var job=iterator.next();try{if(job.advance(world)){job.close();iterator.remove();}}catch(RuntimeException|Error failure){job.close();iterator.remove();throw failure;}}if(pending.isEmpty())PENDING.remove(world);}
        if(!ReactorConfig.ELEMENTALS.get()||world.getGameTime()%ReactorConfig.ELEMENTAL_DELAY.get()!=0)return;
        // Source consumes chance before testing surface world or player presence; no Peaceful gate is added.
        if(world.random.nextInt(ReactorConfig.ELEMENTAL_CHANCE.get())!=0||world.players().isEmpty()||!world.dimensionType().natural())return;
        var player=world.players().get(world.random.nextInt(world.players().size()));var persisted=player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if(!persisted.getBoolean("radMark"))return;
        player.sendSystemMessage(Component.translatable("chat.hbm.elementals.near").withStyle(ChatFormatting.YELLOW));persisted.putBoolean("radMark",false);player.getPersistentData().put(Player.PERSISTED_NBT_TAG,persisted);
        // The source accidentally uses raidAttackDistance, not its declared elementalDistance. Preserve that actual call.
        var job=new Encounter(player.getX(),player.getZ(),FbiConfig.DISTANCE.get(),ReactorConfig.ELEMENTAL_AMOUNT.get());
        try{if(job.advance(world))job.close();else PENDING.computeIfAbsent(world,key->new ArrayList<>()).add(job);}catch(RuntimeException|Error failure){job.close();throw failure;}
    }
    private static final class Encounter implements AutoCloseable {
        final double originX,originZ;final int amount;final LegacyVector vector;final NuclearChunkAccess chunks=new NuclearChunkAccess();
        int index;boolean selected;double x,z;
        Encounter(double x,double z,int distance,int amount){originX=x;originZ=z;vector=LegacyVector.createVectorHelper(distance,0,0);this.amount=amount;}
        boolean advance(ServerLevel world){
            while(index<amount){
                if(!selected){vector.rotateAroundY((float)(Math.PI*2)*world.random.nextFloat());x=originX+vector.xCoord+world.random.nextGaussian();z=originZ+vector.zCoord+world.random.nextGaussian();selected=true;}
                int ix=(int)x,iz=(int)z; // Source deliberately truncates negative X/Z for getHeightValue.
                LevelChunk chunk=chunks.get(world,ix>>4,iz>>4);if(chunk==null)return false;
                int y=chunk.getHeight(Heightmap.Types.WORLD_SURFACE,ix&15,iz&15)+1;var cursor=new BlockPos.MutableBlockPos(ix,y-1,iz);
                // Original getHeightValue is the non-zero light-opacity column, not generic world surface/tall grass.
                while(y>world.getMinBuildHeight()){cursor.setY(y-1);if(chunk.getBlockState(cursor).getLightBlock(world,cursor)!=0)break;y--;}
                var mob=new EntityRADBeast(world);if(index==0)mob.makeLeader();mob.moveTo((float)x,(float)y,(float)z,world.random.nextFloat()*360F,0);
                var check=new MobSpawnEvent.PositionCheck(mob,world,MobSpawnType.EVENT,null);NeoForge.EVENT_BUS.post(check);
                if(check.getResult()!=MobSpawnEvent.PositionCheck.Result.FAIL){world.addFreshEntity(mob);EventHooks.finalizeMobSpawn(mob,world,world.getCurrentDifficultyAt(mob.blockPosition()),MobSpawnType.EVENT,null);}
                selected=false;index++;
            }
            return true;
        }
        @Override public void close(){chunks.close();}
    }
    private static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel world){var jobs=PENDING.remove(world);if(jobs!=null)jobs.forEach(Encounter::close);}}
    private static void stop(ServerStoppingEvent event){for(var iterator=PENDING.entrySet().iterator();iterator.hasNext();){var entry=iterator.next();if(entry.getKey().getServer()==event.getServer()){entry.getValue().forEach(Encounter::close);iterator.remove();}}}
    private ElementalEncounters(){}
}
