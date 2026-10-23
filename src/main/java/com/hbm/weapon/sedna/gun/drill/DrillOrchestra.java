package com.hbm.weapon.sedna.gun.drill;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import java.util.function.BiConsumer;
import net.minecraft.world.item.ItemStack;
/** Actual ORCHESTRA_DRILL server timed sounds and client callback, without loading client classes on a dedicated server. */
public final class DrillOrchestra {
 public static BiConsumer<ItemStack,LambdaContext> CLIENT=(stack,context)->{};
 public static void play(ItemStack stack,LambdaContext context){if(context.entity.level().isClientSide){CLIENT.accept(stack,context);return;}if(ItemGunBaseNT.getLastAnim(stack,context.configIndex)!=GunAnimation.RELOAD)return;int timer=ItemGunBaseNT.getAnimTimer(stack,context.configIndex);switch(timer){case 15->GunSounds.play(context.entity,"hbm:weapon.reload.openLatch",1,1);case 35->GunSounds.play(context.entity,"hbm:weapon.reload.impact",.5F,1);case 60->GunSounds.play(context.entity,"hbm:weapon.reload.revolverClose",1,.75F);case 70->GunSounds.play(context.entity,"hbm:weapon.reload.insertCanister",1,1);case 85->GunSounds.play(context.entity,"hbm:weapon.reload.pressureValve",1,1);default->{}}}
 private DrillOrchestra(){}
}
