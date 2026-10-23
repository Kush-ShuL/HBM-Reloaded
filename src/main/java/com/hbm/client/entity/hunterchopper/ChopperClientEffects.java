package com.hbm.client.entity.hunterchopper;
import com.hbm.client.bomb.nuclear.LegacyRocketFlameParticle;
import com.hbm.entity.mob.hunterchopper.*;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
/** Original null-sound triggers become real entity-bound loops, with the same sound/state/volume/pitch. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ChopperClientEffects {
 private static final Map<Entity,Map<String,Loop>> SOUNDS=new IdentityHashMap<>();private static final Random EXHAUST_RANDOM=new Random();
 public static void tick(HunterChopper chopper){if(chopper.isRemoved()){removed(chopper);return;}if(chopper.isDying()){finish(chopper,"entity.chopperFlyingLoop");if(chopper.tickCount%2==0)ensure(chopper,"entity.chopperCrashingLoop");}else{finish(chopper,"entity.chopperCrashingLoop");ensure(chopper,"entity.chopperFlyingLoop");}}
 public static void tick(ChopperMine mine){if(mine.isRemoved())removed(mine);else ensure(mine,"entity.chopperMineLoop");}
 private static void ensure(Entity entity,String name){var sounds=SOUNDS.computeIfAbsent(entity,e->new HashMap<>());var sound=sounds.get(name);if(sound==null){sound=new Loop(entity,name);sounds.put(name,sound);Minecraft.getInstance().getSoundManager().play(sound);}}
 private static void finish(Entity entity,String name){var sounds=SOUNDS.get(entity);if(sounds!=null){var sound=sounds.remove(name);if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}if(sounds.isEmpty())SOUNDS.remove(entity);}}
 public static void removed(Entity entity){var sounds=SOUNDS.remove(entity);if(sounds!=null)for(var sound:sounds.values()){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}}
 public static void exhaust(double x,double y,double z){var mc=Minecraft.getInstance();if(mc.level==null||mc.player==null||mc.player.distanceToSqr(x,y,z)>350D*350)return;for(int i=0;i<10;i++)mc.particleEngine.add(new LegacyRocketFlameParticle(mc.level,x+EXHAUST_RANDOM.nextGaussian(),y+EXHAUST_RANDOM.nextGaussian(),z+EXHAUST_RANDOM.nextGaussian()));}
 @SubscribeEvent public static void unload(LevelEvent.Unload e){if(e.getLevel().isClientSide())for(var entity:SOUNDS.keySet().toArray(Entity[]::new))removed(entity);}
 private static final class Loop extends AbstractTickableSoundInstance {final Entity owner;Loop(Entity owner,String name){super(HbmSounds.get(name),SoundSource.HOSTILE,RandomSource.create());this.owner=owner;looping=true;delay=0;volume=10;pitch=1;updatePosition();}private void updatePosition(){x=owner.getX();y=owner.getY();z=owner.getZ();}@Override public void tick(){if(owner.isRemoved()){stop();return;}updatePosition();}void finish(){stop();}}
 private ChopperClientEffects(){}
}
