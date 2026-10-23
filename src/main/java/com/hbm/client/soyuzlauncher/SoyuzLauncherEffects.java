package com.hbm.client.soyuzlauncher;

import com.hbm.client.bomb.conventional.SmokeBurstParticles;
import com.hbm.client.bomb.nuclear.LegacyRocketFlameParticle;
import com.hbm.entity.rocket.EntityRocketSoyuz;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Original ready loop, shockRand launchpad smoke, and all five ClientProxy exhaust(mode=soyuz) emitters. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SoyuzLauncherEffects {
    private static final Map<SoyuzLauncherBlockEntity,Loop> LOOPS=new IdentityHashMap<>();
    private static final Random EXHAUST_RANDOM=new Random();
    public static void tick(SoyuzLauncherBlockEntity machine){
        var client=Minecraft.getInstance();if(!(machine.getLevel() instanceof ClientLevel level))return;
        if(!machine.starting)removed(machine);else if(machine.countdown>0){var loop=LOOPS.get(machine);if(loop==null||!client.getSoundManager().isActive(loop)){removed(machine);loop=new Loop(machine);LOOPS.put(machine,loop);client.getSoundManager().play(loop);}loop.keepAlive=0;}
        var p=machine.getBlockPos();if(!level.getEntitiesOfClass(EntityRocketSoyuz.class,new AABB(p.getX()-.5,p.getY(),p.getZ()-.5,p.getX()+1.5,p.getY()+10,p.getZ()+1.5)).isEmpty())SmokeBurstParticles.shockRand(p.getX()+.5,p.getY()-3,p.getZ()+.5,50,level.random.nextGaussian()*3+6);
    }
    public static void rocketTick(EntityRocketSoyuz rocket){
        var client=Minecraft.getInstance();if(!(rocket.level() instanceof ClientLevel level)||client.player==null)return;
        for(var offset:new double[][]{{0,0},{2.75,0},{-2.75,0},{0,2.75},{0,-2.75}}){
            double x=rocket.getX()+offset[0],y=rocket.getY(),z=rocket.getZ()+offset[1];double width=level.random.nextDouble()*.25-.5;
            if(client.player.distanceToSqr(x,y,z)>350D*350)continue;
            var flame=new LegacyRocketFlameParticle(level,x+EXHAUST_RANDOM.nextGaussian()*width,y,z+EXHAUST_RANDOM.nextGaussian()*width);flame.setParticleSpeed(0,-.75+EXHAUST_RANDOM.nextDouble()*.5,0);client.particleEngine.add(flame);
        }
    }
    public static void removed(SoyuzLauncherBlockEntity machine){var loop=LOOPS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())for(var machine:LOOPS.keySet().toArray(SoyuzLauncherBlockEntity[]::new))removed(machine);}
    private static final class Loop extends AbstractTickableSoundInstance {
        final SoyuzLauncherBlockEntity machine;int keepAlive;
        Loop(SoyuzLauncherBlockEntity machine){super(HbmSounds.get("block.soyuzReady"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();looping=true;attenuation=Attenuation.NONE;pitch=1;update();}
        void update(){var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=100*(1-distance/100F);}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>10){finish();return;}update();}
        void finish(){stop();}
    }
    private SoyuzLauncherEffects(){}
}
