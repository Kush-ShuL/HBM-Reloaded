package com.hbm.weapon.sedna.gun.utility;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.machine.fluidtank.mixin.FauxLadderConnectionAccess;
import com.hbm.weapon.sedna.gun.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Original ItemGunChargeThrower tether, including pull, pendulum constraint and secondary brake. */
public final class ItemGunChargeThrower extends ItemGunBaseNT {
 public static final String KEY_LASTHOOK="lasthook";
 public ItemGunChargeThrower(WeaponQuality quality,GunConfig... configs){super(quality,configs);}
 public static int getLastHook(ItemStack stack){return getValueInt(stack,KEY_LASTHOOK);}
 public static void setLastHook(ItemStack stack,int value){setValueInt(stack,KEY_LASTHOOK,value);}
 // 1.7 MathHelper.sqrt_double returned float; the source Vec3NT normalization uses that exact result.
 static double length(Vec3 value){return (float)Math.sqrt(value.lengthSqr());}
 static Vec3 normalize(Vec3 value){double length=length(value);return length<1E-4?Vec3.ZERO:value.scale(1D/length);}
 @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean held){
  super.inventoryTick(stack,level,entity,slot,held);
  if(getState(stack,0)==GunState.RELOADING&&getLastHook(stack)!=-1)setLastHook(stack,-1);
  // Core recomputes its local held flag too; follow actual native hand eligibility, alive/spectator and enabled state.
  if(entity instanceof Player player)held=player.isAlive()&&!player.isSpectator()&&GunSettings.enabled()&&stack==player.getMainHandItem();
  if(held&&entity instanceof Player player){
   var hook=level.getEntity(getLastHook(stack));
   if(hook instanceof EntityBulletBaseMK4 bullet&&!hook.isRemoved()&&bullet.config==ChargeThrowerBullets.HOOK&&bullet.velocity<.01){
    var vector=new Vec3(hook.getX()-player.getX(),hook.getY()-player.getY()-player.getEyeHeight(),hook.getZ()-player.getZ());double line=length(vector);
    if(getPrimary(stack,0)){
     player.setDeltaMovement(player.getDeltaMovement().add(normalize(vector).scale(.1)).add(0,.04,0));
     if(!level.isClientSide&&line<2)hook.discard();
    }else if(!getSecondary(stack,0)){
     var next=player.position().add(0,player.getEyeHeight(),0).add(player.getDeltaMovement());var delta=hook.position().subtract(next);
     if(length(delta)>line){var newNext=hook.position().subtract(normalize(delta).scale(line));var velocity=newNext.subtract(player.position()).subtract(0,player.getEyeHeight(),0);if(length(velocity)<3)player.setDeltaMovement(velocity);}
    }else player.setDeltaMovement(player.getDeltaMovement().scale(.5));
    if(player.getDeltaMovement().y>-.1)player.fallDistance=0;
    if(player instanceof ServerPlayer server)((FauxLadderConnectionAccess)server.connection).hbm$resetFlightTime(0);
   }
  }else if(getLastHook(stack)!=-1)setLastHook(stack,-1);
 }
}
