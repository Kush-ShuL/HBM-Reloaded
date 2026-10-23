package com.hbm.armor.ncrpa;
import com.hbm.armor.rpa.*;
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
/** Original ArmorNCRPAMelee: different25/30timers, real always-gib fatal stab and authored sword-arm bus timings. */
public final class NcrMelee implements PowerArmorMelee {
 public static final NcrMelee INSTANCE=new NcrMelee();private NcrMelee(){}
 @Override public void clickPrimary(ItemStack stack,LambdaContext ctx){PowerArmorGuns.swing(stack,ctx,GunAnimation.CYCLE,25);}
 @Override public void clickSecondary(ItemStack stack,LambdaContext ctx){PowerArmorGuns.swing(stack,ctx,GunAnimation.ALT_CYCLE,30);}
 @Override public void orchestra(ItemStack stack,LambdaContext ctx){
  var owner=ctx.entity;if(owner.level().isClientSide)return;var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
  boolean swings=type==GunAnimation.CYCLE&&(timer==5||timer==15),sweep=type==GunAnimation.ALT_CYCLE&&timer==5;
  if((!swings&&!sweep)||ctx.getPlayer()==null)return;var hit=LegacyEntityDamage.getMouseOver(ctx.getPlayer(),3,.5);if(hit==null)return;
  if(hit instanceof EntityHitResult entityHit&&entityHit.getEntity().isAlive()){
   var target=entityHit.getEntity();float damage=swings?NcrSettings.SWINGS.get().floatValue():NcrSettings.SWEEP.get().floatValue();
   if(target instanceof LivingEntity living){if(living.getMaxHealth()>=100)damage*=NcrSettings.LARGE_MULT.get().floatValue();LegacyEntityDamage.attackEntityFromNT(living,owner.damageSources().playerAttack(ctx.getPlayer()),damage,true,false,swings?0:1.5,swings?5:15,swings?.1F:.25F);if(!living.isAlive())LegacyDeathEffects.gib(living);}
   else target.hurt(owner.damageSources().playerAttack(ctx.getPlayer()),damage);
   owner.level().playSound(null,target.getX(),target.getY(),target.getZ(),HbmSounds.get("weapon.fire.stab"),SoundSource.PLAYERS,1,.9F+owner.getRandom().nextFloat()*.2F);
  }else if(hit instanceof BlockHitResult blockHit&&hit.getType()==HitResult.Type.BLOCK){var state=owner.level().getBlockState(blockHit.getBlockPos());var sound=state.getSoundType(owner.level(),blockHit.getBlockPos(),owner).getStepSound();var position=hit.getLocation();owner.level().playSound(null,position.x,position.y,position.z,sound,SoundSource.BLOCKS,2,.9F+owner.getRandom().nextFloat()*.2F);}
 }
 @Override public BusAnimation playAnim(ItemStack stack,GunAnimation type){
  if(type==GunAnimation.EQUIP)return new BusAnimation().addBus("EQUIP",new BusAnimationSequence().setPos(-1,0,0).addPos(0,0,0,750,IType.SIN_DOWN));
  if(type==GunAnimation.CYCLE)return new BusAnimation().addBus("SWINGRIGHT",new BusAnimationSequence().addPos(1,0,0,250,IType.SIN_DOWN).addPos(0,0,0,500,IType.SIN_FULL)).addBus("SWINGLEFT",new BusAnimationSequence().addPos(0,0,0,500).addPos(1,0,0,250,IType.SIN_DOWN).addPos(0,0,0,500,IType.SIN_FULL));
  if(type==GunAnimation.ALT_CYCLE)return new BusAnimation().addBus("SWEEPTURN",new BusAnimationSequence().addPos(1,0,0,100,IType.LINEAR).hold(350).addPos(0,0,0,500,IType.LINEAR)).addBus("SWEEPCUT",new BusAnimationSequence().hold(100).addPos(1,0,0,250,IType.SIN_DOWN).hold(100).addPos(0,0,0,500,IType.SIN_FULL));
  return null;
 }
}
