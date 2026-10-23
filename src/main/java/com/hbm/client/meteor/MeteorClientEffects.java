package com.hbm.client.meteor;

import com.hbm.client.bomb.nuclear.LegacyRocketFlameParticle;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

/** EntityMeteor's client branch and exactly ClientProxy.exhaust(mode=meteor). */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class MeteorClientEffects {
    private static final Random EXHAUST_RANDOM=new Random();
    private static final Map<Entity,FlightAudio> AUDIO=new IdentityHashMap<>();
    public static void tick(Entity meteor,boolean tails){
        if(meteor.isRemoved()){removed(meteor);return;}
        var minecraft=Minecraft.getInstance();
        if(!(meteor.level() instanceof ClientLevel level))return;
        var audio=AUDIO.computeIfAbsent(meteor,ignored->new FlightAudio());
        var manager=minecraft.getSoundManager();
        if(audio.sound!=null&&manager.isActive(audio.sound)){
            audio.x=(float)meteor.getX();audio.y=(float)(meteor.getY()+meteor.getBbHeight()/2);audio.z=(float)meteor.getZ();
            audio.sound.keepAlive(audio.x,audio.y,audio.z);
        }else if(minecraft.player!=null&&minecraft.player.distanceToSqr(meteor)<210D*210){
            // Source starts a newly created AudioDynamic at(0,0,0), updating its position on the next active tick.
            if(audio.sound!=null){audio.sound.finish();manager.stop(audio.sound);}
            audio.sound=new MeteorFallingSound(audio.x,audio.y,audio.z);manager.play(audio.sound);
        }
        if(!tails||minecraft.player==null)return;
        var motion=meteor.getDeltaMovement();double x=meteor.getX()-motion.x,y=meteor.getY()-motion.y,z=meteor.getZ()-motion.z;
        if(minecraft.player.distanceToSqr(x,y,z)>350D*350)return;
        for(int i=0;i<10;i++)minecraft.particleEngine.add(new LegacyRocketFlameParticle(level,
                x+EXHAUST_RANDOM.nextGaussian(),y+EXHAUST_RANDOM.nextGaussian(),z+EXHAUST_RANDOM.nextGaussian()));
    }
    public static void removed(Entity meteor){var audio=AUDIO.remove(meteor);if(audio!=null&&audio.sound!=null){audio.sound.finish();Minecraft.getInstance().getSoundManager().stop(audio.sound);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var meteor:AUDIO.keySet().toArray(Entity[]::new))removed(meteor);}}
    private static final class FlightAudio {private MeteorFallingSound sound;private float x,y,z;}
    private MeteorClientEffects(){}
}
