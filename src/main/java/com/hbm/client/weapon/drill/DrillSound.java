package com.hbm.client.weapon.drill;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
/** Actual ORCHESTRA_DRILL: SPEED volume/pitch, source electric/combustion sound,15block linear range,25tick keepalive. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DrillSound {
 private static final Map<LivingEntity,Loop> LOOPS=new IdentityHashMap<>();
 public static void orchestra(ItemStack stack,LambdaContext context){
  var owner=context.entity;var manager=Minecraft.getInstance().getSoundManager();var running=LOOPS.get(owner);double speed=HbmAnimations.getRelevantTransformation("SPEED")[0];
  if(speed>0){
   if(running==null||!manager.isActive(running)){var loop=new Loop(owner,XWeaponModManager.hasUpgrade(stack,context.configIndex,228),(float)speed);LOOPS.put(owner,loop);manager.play(loop);}
   if(running!=null&&manager.isActive(running))running.keepAlive((float)speed);
  }
  var type=ItemGunBaseNT.getLastAnim(stack,context.configIndex);if(type!=GunAnimation.CYCLE&&type!=GunAnimation.CYCLE_DRY){var loop=LOOPS.remove(owner);if(loop!=null){loop.finish();manager.stop(loop);}}
 }
 @SubscribeEvent public static void tick(ClientTickEvent.Post event){if(Minecraft.getInstance().level==null){LOOPS.values().forEach(Loop::finish);LOOPS.clear();}else LOOPS.entrySet().removeIf(entry->entry.getValue().isStopped());}
 private static final class Loop extends AbstractTickableSoundInstance {
  private final LivingEntity owner;private float gain;private int sinceKeepAlive;
  Loop(LivingEntity owner,boolean electric,float speed){super(HbmSounds.get(electric?"block.largeTurbineRunning":"block.engine"),SoundSource.PLAYERS,RandomSource.create());this.owner=owner;gain=speed;looping=true;attenuation=Attenuation.NONE;pitch=1;follow();}
  void keepAlive(float speed){sinceKeepAlive=0;gain=speed;pitch=speed;follow();}
  private void follow(){x=owner.getX();y=owner.getY();z=owner.getZ();var player=Minecraft.getInstance().player;volume=player==null?0:gain*(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15);}
  @Override public void tick(){if(owner.isRemoved()||!owner.isAlive()||sinceKeepAlive>25)finish();sinceKeepAlive++;follow();}void finish(){stop();}
 }
 private DrillSound(){}
}
