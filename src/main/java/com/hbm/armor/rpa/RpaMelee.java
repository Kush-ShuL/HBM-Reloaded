package com.hbm.armor.rpa;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.death.LegacyDeathEffects;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
/** Source ArmorRPAMelee server timings, SEDNA piercing, RNG/death and exact bus keyframes. */
public final class RpaMelee implements PowerArmorMelee {
 public static final RpaMelee INSTANCE=new RpaMelee();private RpaMelee(){}
 @Override public void clickPrimary(ItemStack stack,LambdaContext ctx){PowerArmorGuns.swing(stack,ctx,GunAnimation.CYCLE,14);}
 @Override public void clickSecondary(ItemStack stack,LambdaContext ctx){PowerArmorGuns.swing(stack,ctx,GunAnimation.ALT_CYCLE,20);}
 @Override public void orchestra(ItemStack stack,LambdaContext ctx){var entity=ctx.entity;if(entity.level().isClientSide)return;var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
  if(type==GunAnimation.CYCLE&&timer==14&&ItemGunBaseNT.getPrimary(stack,0))PowerArmorGuns.swing(stack,ctx,GunAnimation.CYCLE,14);
  boolean punches=type==GunAnimation.CYCLE&&(timer==3||timer==9),slap=type==GunAnimation.ALT_CYCLE&&timer==8;
  if((!punches&&!slap)||ctx.getPlayer()==null)return;var hit=LegacyEntityDamage.getMouseOver(ctx.getPlayer(),3D,.5D);if(hit==null)return;
  if(hit instanceof EntityHitResult entityHit){var target=entityHit.getEntity();float damage=punches?RpaSettings.SWINGS.get().floatValue():RpaSettings.SLAP.get().floatValue();
   if(target instanceof LivingEntity living){if(living.getMaxHealth()>=100)damage*=RpaSettings.LARGE_MULT.get().floatValue();LegacyEntityDamage.attackEntityFromNT(living,entity.damageSources().playerAttack(ctx.getPlayer()),damage,true,false,punches?0:1.5,punches?5:15,punches?.1F:.25F);if(living.getRandom().nextInt(slap?3:10)==0&&!living.isAlive())LegacyDeathEffects.gib(living);}
   else target.hurt(entity.damageSources().playerAttack(ctx.getPlayer()),damage);
   entity.level().playSound(null,target.getX(),target.getY(),target.getZ(),HbmSounds.get("weapon.fire.smack"),SoundSource.PLAYERS,1,.9F+entity.getRandom().nextFloat()*.2F);
  }else if(hit instanceof BlockHitResult blockHit&&hit.getType()==HitResult.Type.BLOCK){var state=entity.level().getBlockState(blockHit.getBlockPos());var sound=state.getSoundType(entity.level(),blockHit.getBlockPos(),entity).getStepSound();var at=hit.getLocation();entity.level().playSound(null,at.x,at.y,at.z,sound,SoundSource.BLOCKS,2,.9F+entity.getRandom().nextFloat()*.2F);}
 }
 @Override public BusAnimation playAnim(ItemStack stack,GunAnimation type){
  if(type==GunAnimation.EQUIP)return new BusAnimation().addBus("EQUIP",new BusAnimationSequence().setPos(-1,0,0).addPos(0,0,0,250,IType.SIN_DOWN));
  if(type==GunAnimation.CYCLE)return new BusAnimation().addBus("SWINGRIGHT",new BusAnimationSequence().addPos(1,0,0,150,IType.SIN_DOWN).addPos(0,0,0,250,IType.SIN_FULL)).addBus("SWINGLEFT",new BusAnimationSequence().addPos(0,0,0,300).addPos(1,0,0,150,IType.SIN_DOWN).addPos(0,0,0,250,IType.SIN_FULL));
  if(type==GunAnimation.ALT_CYCLE)return new BusAnimation().addBus("SLAPTURN",new BusAnimationSequence().addPos(1,0,0,250,IType.LINEAR).hold(150).addPos(0,0,0,350,IType.LINEAR)).addBus("SLAP",new BusAnimationSequence().hold(250).addPos(1,0,0,150,IType.SIN_DOWN).addPos(0,0,0,350,IType.SIN_FULL));return null;
 }
}
