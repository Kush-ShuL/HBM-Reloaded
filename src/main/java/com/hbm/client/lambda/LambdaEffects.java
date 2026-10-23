package com.hbm.client.lambda;

import com.hbm.client.bomb.nuclear.LegacyRocketFlameParticle;
import com.hbm.client.particle.LegacyCoolingTowerParticle;
import com.hbm.entity.rocket.EntityRocketLambda;
import com.hbm.machine.lambda.LambdaEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Four original keepalive loops, exact endpoint sounds, shared tower steam and the real lambda exhaust/contrail modes. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class LambdaEffects {
    private static final Map<LambdaEntity,Loop[]> LOOPS=new IdentityHashMap<>();
    private static final Random EXHAUST_RANDOM=new Random();
    public static void tick(LambdaEntity machine){
        var client=Minecraft.getInstance();if(!(machine.getLevel() instanceof ClientLevel level))return;var p=machine.getBlockPos();
        if(machine.erected&&machine.hasOxidizer()&&client.player!=null&&client.player.distanceToSqr(p.getX()+.5,p.getY()+2,p.getZ()+.5)<=100D*100){int life=70+level.random.nextInt(30);double x=p.getX()+.5+level.random.nextGaussian()*.25,z=p.getZ()+.5+level.random.nextGaussian()*.25;for(int i=0;i<3;i++){var steam=new LegacyCoolingTowerParticle(level,x,p.getY()+2,z);steam.setLift(0);steam.setBaseScale(.5F);steam.setMaxScale(2);steam.setLife(life);steam.noWind();steam.alphaMod(2);steam.setStrafe(.075F);client.particleEngine.add(steam);}}
        boolean[] running={machine.syncPositions[LambdaEntity.DOORS]>0&&machine.syncPositions[LambdaEntity.DOORS]<3,!machine.finishedMoving(LambdaEntity.ERECTOR)&&machine.wasMoving(LambdaEntity.ERECTOR),machine.syncPositions[LambdaEntity.ROTOR]>0&&machine.syncPositions[LambdaEntity.ROTOR]<180,machine.countdown>0};
        var loops=LOOPS.computeIfAbsent(machine,ignored->new Loop[4]);for(int i=0;i<4;i++){
            if(running[i]){if(loops[i]==null||!client.getSoundManager().isActive(loops[i])){if(loops[i]!=null)loops[i].finish();loops[i]=new Loop(machine,i);client.getSoundManager().play(loops[i]);}loops[i].keepAlive=0;}
            else if(loops[i]!=null){loops[i].finish();client.getSoundManager().stop(loops[i]);loops[i]=null;if(Math.abs(machine.positions[LambdaEntity.ERECTOR]-machine.prevPositions[LambdaEntity.ERECTOR])>5)continue;Vec3 pos=position(machine,i);if(i==0)stop(level,pos,"door.garage_stop",35,1);if(i==1&&machine.target[LambdaEntity.ERECTOR]==25)stop(level,pos,"door.wgh_big_stop",25,.5F);if(i==1&&machine.target[LambdaEntity.ERECTOR]!=25)stop(level,pos,"door.wgh_stop",25,1);if(i==2)stop(level,pos,"door.wgh_big_stop",25,.75F);}
        }
        if(machine.wasMoving(LambdaEntity.CLAMPS)&&machine.syncPositions[LambdaEntity.CLAMPS]==90)stop(level,new Vec3(p.getX()+.5,p.getY()+12,p.getZ()+.5),"door.sliding_seal_stop",25,.75F);
    }
    private static void stop(ClientLevel level,Vec3 pos,String sound,float volume,float pitch){level.playLocalSound(pos.x,pos.y,pos.z,HbmSounds.get(sound),SoundSource.BLOCKS,volume,pitch,false);}
    private static Vec3 position(LambdaEntity machine,int index){var p=machine.getBlockPos();return new Vec3(p.getX()+.5,p.getY()+3+(index==2?12:0),p.getZ()+.5);}
    public static void rocketTick(EntityRocketLambda rocket){
        var client=Minecraft.getInstance();if(!(rocket.level() instanceof ClientLevel level)||client.player==null)return;double x=rocket.getX(),y=rocket.getY()+1,z=rocket.getZ();
        if(client.player.distanceToSqr(x,y,z)<=350D*350){var flame=new LegacyRocketFlameParticle(level,x+EXHAUST_RANDOM.nextGaussian()*0,y,z+EXHAUST_RANDOM.nextGaussian()*0).setScale(1.5F);flame.setParticleSpeed(0,-1+EXHAUST_RANDOM.nextDouble()*.25,0);client.particleEngine.add(flame);}
        if(rocket.tickCount%10==0){double angle=Math.toRadians(45*rocket.sourceRandom().nextInt(8));double vx=Math.cos(angle),vz=-Math.sin(angle);x=rocket.getX()-vx*.5;y=rocket.getY();z=rocket.getZ()-vz*.5;int life=60+rocket.sourceRandom().nextInt(20);if(client.player.distanceToSqr(x,y,z)<=350D*350){var trail=new LegacyRocketFlameParticle(level,x,y,z).setScale(1).setMaxAge(life);trail.setParticleSpeed(vx,rocket.getDeltaMovement().y-.5,vz);client.particleEngine.add(trail);}}
    }
    public static void removed(LambdaEntity machine){var loops=LOOPS.remove(machine);if(loops!=null)for(var loop:loops)if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())for(var machine:LOOPS.keySet().toArray(LambdaEntity[]::new))removed(machine);}
    private static final class Loop extends AbstractTickableSoundInstance {
        final LambdaEntity machine;final int index;int keepAlive;
        Loop(LambdaEntity machine,int index){super(HbmSounds.get(new String[]{"door.garage_move","door.wgh_start","door.wgh_big_start","alarm.regularSiren"}[index]),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;this.index=index;looping=true;attenuation=Attenuation.NONE;pitch=1;update();}
        void update(){var p=position(machine,index);x=p.x;y=p.y;z=p.z;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=(index==3?10:2)*(1-distance/(index==3?50F:35F));}
        @Override public void tick(){if(machine.isRemoved()||keepAlive>(index==3?20:10)){finish();return;}keepAlive++;update();}void finish(){stop();}
    }
    private LambdaEffects(){}
}
