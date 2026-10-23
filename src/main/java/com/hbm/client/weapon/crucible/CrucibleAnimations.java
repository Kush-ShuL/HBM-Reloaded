package com.hbm.client.weapon.crucible;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.AnimationEnums.ToolAnimation;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.crucible.CrucibleItem;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
/** Original two ToolAnimation buses and overlap gate, on the sole HbmAnimations hotbar rail0. */
public final class CrucibleAnimations {
 private static final Random SOURCE_RANDOM=new Random();
 static void receive(ToolAnimation type){var player=Minecraft.getInstance().player;if(player==null)return;var stack=player.getMainHandItem();if(!(stack.getItem() instanceof CrucibleItem))return;var animation=animation(type);if(animation!=null)HbmAnimations.hotbar[player.getInventory().selected][0]=new HbmAnimations.Animation(stack.getItem().getDescriptionId(),System.currentTimeMillis(),animation);}
 private static BusAnimation animation(ToolAnimation type){
  if(type==ToolAnimation.EQUIP)return new BusAnimation().addBus("GUARD_ROT",new BusAnimationSequence().addPos(90,0,1,0).addPos(90,0,1,800).addPos(0,0,1,50));
  if(type==ToolAnimation.SWING&&HbmAnimations.getRelevantTransformation("SWING_ROT")[0]==0){
   int offset=SOURCE_RANDOM.nextInt(80)-20;
   Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(HbmSounds.get("weapon.cSwing"),.8F+SOURCE_RANDOM.nextFloat()*.2F));
   return new BusAnimation().addBus("SWING_ROT",new BusAnimationSequence().addPos(90-offset,90-offset,35,75).addPos(90+offset,90-offset,-45,150).addPos(0,0,0,500))
    .addBus("SWING_TRANS",new BusAnimationSequence().addPos(-3,0,0,75).addPos(8,0,0,150).addPos(0,0,0,500));
  }
  return null;
 }
 private CrucibleAnimations(){}
}
