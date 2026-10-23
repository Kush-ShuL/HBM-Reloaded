package com.hbm.weapon.sedna.gun.rifle762;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.*;
/** Original Carbine/MAS36 inspect tick15 bayonet strike, with the original 3-block mouse-over and x/z knockback. */
public final class Rifle762Melee {
 public static void stab(LambdaContext context,float damage){var player=context.getPlayer();if(player==null)return;var hit=LegacyEntityDamage.getMouseOver(player,3);
  if(hit instanceof EntityHitResult entityHit){var target=entityHit.getEntity();target.hurt(player.damageSources().playerAttack(player),damage);var motion=target.getDeltaMovement();target.setDeltaMovement(motion.x*2,motion.y,motion.z*2);target.level().playSound(null,target.getX(),target.getY(),target.getZ(),HbmSounds.get("weapon.fire.stab"),target.getSoundSource(),1,.9F+context.entity.getRandom().nextFloat()*.2F);}
  else if(hit instanceof BlockHitResult blockHit){var pos=blockHit.getBlockPos();var state=player.level().getBlockState(pos);var point=hit.getLocation();player.level().playSound(null,point.x,point.y,point.z,state.getSoundType(player.level(),pos,context.entity).getStepSound(),SoundSource.BLOCKS,2,.9F+context.entity.getRandom().nextFloat()*.2F);}
 }
 private Rifle762Melee(){}
}
