package com.hbm.client.launchpadsoyuz;

import com.hbm.client.particle.LegacyFluidEffectsClient;
import com.hbm.client.bomb.conventional.SmokeBurstParticles;
import com.hbm.entity.rocket.EntityRocketSoyuz;
import com.hbm.machine.launchpadsoyuz.LaunchpadSoyuzEntity;
import com.hbm.machine.launchpadsoyuz.LaunchpadSoyuzEntity.SoyuzStatus;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Original three10-tick keepalive loops and exact translated endpoints, using the sole existing tower/shock particles. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class LaunchpadSoyuzEffects {
    private static final Map<LaunchpadSoyuzEntity,Loop[]> LOOPS=new IdentityHashMap<>();
    public static void tick(LaunchpadSoyuzEntity machine){
        var client=Minecraft.getInstance();if(!(machine.getLevel() instanceof ClientLevel level))return;var p=machine.getBlockPos();var engine=machine.enginePosition();
        if((machine.soyuzStatus==SoyuzStatus.FUELING||machine.soyuzStatus==SoyuzStatus.READY||machine.soyuzStatus==SoyuzStatus.LAUNCHING)&&machine.hasOxidizer()&&client.player!=null&&client.player.distanceToSqr(p.getX()+.5,p.getY()+2,p.getZ()+.5)<=100D*100){
            int life=70+level.random.nextInt(30);var packet=new LegacyFluidEffects.Packet(false,engine.x+level.random.nextGaussian()*.75,p.getY()+4,engine.z+level.random.nextGaussian()*.75,0,0,0,0,new LegacyFluidEffects.Tower(0,.5F,2,life).withoutWind().alpha(2).strafe(.075F));for(int i=0;i<3;i++)LegacyFluidEffectsClient.receive(packet);
        }
        var rocketArea=new AABB(engine.x-1,p.getY()+4,engine.z-1,engine.x+1,p.getY()+14,engine.z+1);
        if(!level.getEntitiesOfClass(EntityRocketSoyuz.class,rocketArea).isEmpty()||machine.soyuzStatus==SoyuzStatus.LAUNCHING&&machine.countdown<=20)SmokeBurstParticles.shockRand(engine.x,p.getY()+1,engine.z,50,level.random.nextGaussian()*3+6);
        boolean[] running={machine.soyuzStatus==SoyuzStatus.LAUNCHING,machine.power>=LaunchpadSoyuzEntity.CONSUMPTION&&machine.positions[LaunchpadSoyuzEntity.INDEX_CARRIAGE]>0&&machine.positions[LaunchpadSoyuzEntity.INDEX_CARRIAGE]<1,machine.power>=LaunchpadSoyuzEntity.CONSUMPTION&&machine.positions[LaunchpadSoyuzEntity.INDEX_ROTOR]>0&&machine.positions[LaunchpadSoyuzEntity.INDEX_ROTOR]<1};
        var loops=LOOPS.computeIfAbsent(machine,ignored->new Loop[3]);for(int i=0;i<3;i++){
            if(running[i]){if(loops[i]==null||!client.getSoundManager().isActive(loops[i])){if(loops[i]!=null)loops[i].finish();loops[i]=new Loop(machine,i);client.getSoundManager().play(loops[i]);}loops[i].keepAlive=0;loops[i].update();}
            else if(loops[i]!=null){loops[i].finish();client.getSoundManager().stop(loops[i]);loops[i]=null;var at=position(machine,i);if(i>0)level.playLocalSound(at.x,at.y,at.z,HbmSounds.get(i==1?"door.garage_stop":"door.wgh_big_stop"),SoundSource.BLOCKS,35,i==1?1:.75F,false);}
        }
    }
    private static Vec3 position(LaunchpadSoyuzEntity machine,int index){var result=machine.enginePosition();if(index==0)return result;double distance=(index==1?24:18)+20.5*(1-machine.positions[LaunchpadSoyuzEntity.INDEX_CARRIAGE]);var rot=DummyableBlock.facing(machine.getBlockState()).getClockWise();return result.add(-rot.getStepX()*distance,0,-rot.getStepZ()*distance);}
    public static void removed(LaunchpadSoyuzEntity machine){var loops=LOOPS.remove(machine);if(loops!=null)for(var loop:loops)if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())for(var machine:LOOPS.keySet().toArray(LaunchpadSoyuzEntity[]::new))removed(machine);}
    private static final class Loop extends AbstractTickableSoundInstance {
        final LaunchpadSoyuzEntity machine;final int index;int keepAlive;
        Loop(LaunchpadSoyuzEntity machine,int index){super(HbmSounds.get(new String[]{"block.soyuzReady","door.garage_move","door.wgh_big_start"}[index]),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;this.index=index;looping=true;attenuation=Attenuation.NONE;pitch=index==2?.75F:1;update();}
        void update(){var at=position(machine,index);x=at.x;y=at.y;z=at.z;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=2*Math.max(0,1-distance/new float[]{100,35,50}[index]);}
        @Override public void tick(){if(machine.isRemoved()||keepAlive>10){finish();return;}keepAlive++;update();}void finish(){stop();}
    }
    private LaunchpadSoyuzEffects(){}
}
