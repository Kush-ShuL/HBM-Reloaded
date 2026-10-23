package com.hbm.armor.ncrpa;
import com.hbm.armor.rpa.PowerArmorRanged;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.MagazineBelt;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
/** Actual ArmorNCRPARanged consumes the first true rocket type from inventory/bags; no extra HE/fake magazine. */
public final class NcrRanged implements PowerArmorRanged {
 public static final NcrRanged INSTANCE=new NcrRanged();
 public static final MagazineBelt ROCKET_STEER=new MagazineBelt(),ROCKET=new MagazineBelt();
 private NcrRanged(){}
 @Override public void clickPrimary(ItemStack stack,LambdaContext context){fireRocket(stack,context,true);}
 @Override public void clickSecondary(ItemStack stack,LambdaContext context){fireRocket(stack,context,false);}
 public static void fireRocket(ItemStack stack,LambdaContext context,boolean steer){
  var player=context.getPlayer();if(player==null||player.level().isClientSide)return;
  if(ItemGunBaseNT.getState(stack,0)!=GunState.IDLE)return;
  var magazine=steer?ROCKET_STEER:ROCKET;if(magazine.acceptedBullets.isEmpty())magazine.addConfigs(steer?NcrRocketBullets.STEER:NcrRocketBullets.PLAIN);
  var inventory=new GunInventory(player);var config=magazine.getType(stack,inventory);int amount=magazine.getAmount(stack,inventory);
  if(amount>0){
   magazine.useUpAmmo(stack,inventory,1);
   var rocket=new EntityBulletBaseMK4(player,config,NcrSettings.RANGED.get().floatValue(),0,.25F*(player.getRandom().nextBoolean()?-1:1),0,0);player.level().addFreshEntity(rocket);
   ItemGunBaseNT.setState(stack,0,GunState.COOLDOWN);ItemGunBaseNT.setTimer(stack,0,10);
   player.level().playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("weapon.rpgShoot"),SoundSource.PLAYERS,.5F,.9F+player.getRandom().nextFloat()*.2F);player.inventoryMenu.broadcastChanges();
  }else{ItemGunBaseNT.setState(stack,0,GunState.COOLDOWN);ItemGunBaseNT.setTimer(stack,0,10);player.level().playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("weapon.reload.dryFireClick"),SoundSource.PLAYERS,1,1);}
 }
}
