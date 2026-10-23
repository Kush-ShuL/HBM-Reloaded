package com.hbm.weapon.sedna.gun.missilelauncher;
import java.util.function.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.Lego;
import com.hbm.weapon.sedna.gun.mags.MagazineSingleReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.weapon.sedna.gun.stinger.ItemGunStinger;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
/** Actual X5846 gun_missile_launcher is a standard Sedna item with instant ADS targeting. */
public final class MissileLauncherFactory {
 private MissileLauncherFactory(){}
 public static ItemGunBaseNT create(){return new ItemGunBaseNT(WeaponQuality.A_SIDE,new GunConfig()
  .dura(500).draw(20).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(false)
  .rec(new Receiver(0).dmg(MissileLauncherSettings.DAMAGE).spreadHipfire(0F).delay(5).reload(48).jam(33)
   .sound("hbm:weapon.rpgShoot",1F,1F).mag(new MagazineSingleReload(0,1).addConfigs(MissileLauncherBullets.ROCKETS))
   .offset(1,-.0625*1.5,-.1875D).setupStandardFire().recoil((stack,ctx)->{}))
  .setupStandardConfiguration().pp((stack,ctx)->{
   if(ItemGunBaseNT.getIsAiming(stack)){
    int target=ItemGunStinger.getLockonTarget(ctx.getPlayer(),150D,20D);
    if(target!=-1){ItemGunBaseNT.setLockonTarget(stack,target);ItemGunBaseNT.setIsLockedOn(stack,true);}
   }
   Lego.LAMBDA_STANDARD_CLICK_PRIMARY.accept(stack,ctx);
   ItemGunBaseNT.setIsLockedOn(stack,false);
  }).anim(LAMBDA_MISSILE_LAUNCHER_ANIMS).orchestra(MissileLauncherFactory::orchestra))
  .setDefaultAmmo(PistolAmmo.ROCKET_HEAT,5);}
 private static void orchestra(ItemStack stack,LambdaContext ctx){
  if(ctx.entity.level().isClientSide)return;
  var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
  if(type==GunAnimation.CYCLE&&timer==0)GunNetwork.flash(ctx.entity);
  if(type==GunAnimation.CYCLE_DRY&&timer==0)GunSounds.play(ctx.entity,"hbm:weapon.reload.dryFireClick",1F,1.25F);
  if(type==GunAnimation.RELOAD){
   if(timer==0)GunSounds.play(ctx.entity,"hbm:weapon.reload.boltOpen",1F,.9F);
   if(timer==30)GunSounds.play(ctx.entity,"hbm:weapon.reload.insertCanister",1F,1F);
   if(timer==42)GunSounds.play(ctx.entity,"hbm:weapon.reload.boltClose",1F,.9F);
  }
  if(type==GunAnimation.JAMMED||type==GunAnimation.INSPECT){
   if(timer==0)GunSounds.play(ctx.entity,"hbm:weapon.reload.boltOpen",1F,.9F);
   if(timer==27)GunSounds.play(ctx.entity,"hbm:weapon.reload.boltClose",1F,.9F);
  }
 }
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MISSILE_LAUNCHER_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
		case RELOAD: return new BusAnimation()
				.addBus("BARREL", new BusAnimationSequence().addPos(0, 0, 1.5, 150).addPos(0, 0, 1.5, 2100).addPos(0, 0, 0, 150))
				.addBus("OPEN", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(90, 0, 0, 500, IType.SIN_FULL).addPos(90, 0, 0, 1000).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 2250).addPos(-1, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus("MISSILE", new BusAnimationSequence().addPos(-10, 0, 0, 0).addPos(-10, 0, 0, 750).addPos(3, 0, 2, 0).addPos(0, 0, -6, 350, IType.SIN_FULL).addPos(0, 0, 0, 350, IType.SIN_UP));
		case JAMMED:
		case INSPECT: return new BusAnimation()
				.addBus("BARREL", new BusAnimationSequence().addPos(0, 0, 1.5, 150).addPos(0, 0, 1.5, 1350).addPos(0, 0, 0, 150))
				.addBus("OPEN", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(90, 0, 0, 500, IType.SIN_FULL).addPos(90, 0, 0, 250).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 1500).addPos(-1, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_UP));
		}
		return null;
	};
}
