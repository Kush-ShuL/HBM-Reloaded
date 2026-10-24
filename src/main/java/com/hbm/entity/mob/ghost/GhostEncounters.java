package com.hbm.entity.mob.ghost;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.exposure.HbmExposure;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Source always-enabled digamma haunting branch, no extra radiation/timer/config backend. */
public final class GhostEncounters {
    private static final Map<ServerLevel,List<Spawn>> PENDING=new IdentityHashMap<>();
    public static void register(){NeoForge.EVENT_BUS.addListener(GhostEncounters::unload);NeoForge.EVENT_BUS.addListener(GhostEncounters::stop);}
    public static void update(ServerLevel world){
        var jobs=PENDING.get(world);if(jobs!=null){for(var iterator=jobs.iterator();iterator.hasNext();){var job=iterator.next();try{if(job.advance(world)){job.close();iterator.remove();}}catch(RuntimeException|Error failure){job.close();iterator.remove();throw failure;}}if(jobs.isEmpty())PENDING.remove(world);}
        if(world.getGameTime()%20!=0)return;
        if(world.random.nextInt(5)!=0||world.players().isEmpty()||!world.dimensionType().natural())return;
        var player=world.players().get(world.random.nextInt(world.players().size()));if(HbmExposure.digamma(player)<=0)return;
        var vector=LegacyVector.createVectorHelper(75,0,0);vector.rotateAroundY((float)(Math.PI*2)*world.random.nextFloat());
        double x=player.getX()+vector.xCoord+world.random.nextGaussian(),z=player.getZ()+vector.zCoord+world.random.nextGaussian();var job=new Spawn(x,z);
        try{if(job.advance(world))job.close();else PENDING.computeIfAbsent(world,key->new ArrayList<>()).add(job);}catch(RuntimeException|Error failure){job.close();throw failure;}
    }
    private static final class Spawn implements AutoCloseable {
        final double x,z;final NuclearChunkAccess chunks=new NuclearChunkAccess();
        Spawn(double x,double z){this.x=x;this.z=z;}
        boolean advance(ServerLevel world){
            int ix=(int)x,iz=(int)z;var chunk=chunks.get(world,ix>>4,iz>>4);if(chunk==null)return false;
            int y=chunk.getHeight(Heightmap.Types.WORLD_SURFACE,ix&15,iz&15)+1;var cursor=new BlockPos.MutableBlockPos(ix,y-1,iz);
            while(y>world.getMinBuildHeight()){cursor.setY(y-1);if(chunk.getBlockState(cursor).getLightBlock(world,cursor)!=0)break;y--;}
            var ghost=new EntityGhost(world);ghost.moveTo((float)x,(float)y,(float)z,world.random.nextFloat()*360F,0);
            var check=new MobSpawnEvent.PositionCheck(ghost,world,MobSpawnType.EVENT,null);NeoForge.EVENT_BUS.post(check);
            if(check.getResult()!=MobSpawnEvent.PositionCheck.Result.FAIL){world.addFreshEntity(ghost);EventHooks.finalizeMobSpawn(ghost,world,world.getCurrentDifficultyAt(ghost.blockPosition()),MobSpawnType.EVENT,null);}return true;
        }
        @Override public void close(){chunks.close();}
    }
    private static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel world){var jobs=PENDING.remove(world);if(jobs!=null)jobs.forEach(Spawn::close);}}
    private static void stop(ServerStoppingEvent event){for(var iterator=PENDING.entrySet().iterator();iterator.hasNext();){var entry=iterator.next();if(entry.getKey().getServer()==event.getServer()){entry.getValue().forEach(Spawn::close);iterator.remove();}}}
    private GhostEncounters(){}
}
