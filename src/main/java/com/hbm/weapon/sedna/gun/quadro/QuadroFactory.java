package com.hbm.weapon.sedna.gun.quadro;
import java.util.function.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
public final class QuadroFactory {
 private QuadroFactory(){}
 public static ItemGunBaseNT create(){return new ItemGunBaseNT(WeaponQuality.A_SIDE,new GunConfig()
  .dura(400).draw(7).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(false)
  .rec(new Receiver(0).dmg(QuadroSettings.DAMAGE).spreadHipfire(0F).delay(10).reload(55).jam(40)
   .sound("hbm:weapon.rpgShoot",1F,1F).mag(new MagazineFullReload(0,4).addConfigs(QuadroBullets.ROCKETS))
   .offset(1,-.0625*1.5,-.1875D).setupStandardFire().recoil((stack,ctx)->{}))
  .setupStandardConfiguration().anim(LAMBDA_QUADRO_ANIMS).orchestra((stack,ctx)->{
   if(ctx.entity.level().isClientSide)return;
   var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
   if(type==GunAnimation.CYCLE&&timer==0)GunNetwork.flash(ctx.entity);
   if(type==GunAnimation.RELOAD&&timer==30)GunSounds.play(ctx.entity,"hbm:weapon.reload.insertCanister",1F,1F);
  })).setDefaultAmmo(PistolAmmo.ROCKET_HE,4);}
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_QUADRO_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50).addPos(0, 0, 0, 50));
		case RELOAD: return new BusAnimation()
				.addBus("RELOAD_ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 500, IType.SIN_FULL).addPos(0, 0, 60, 1500).addPos(0, 0, 0, 750, IType.SIN_FULL))
				.addBus("RELOAD_PUSH", new BusAnimationSequence().addPos(-1, -1, 0, 0).addPos(-1, -1, 0, 500).addPos(-1, 0, 0, 350).addPos(0, 0, 0, 1000));
		case JAMMED:
		case INSPECT: return new BusAnimation()
				.addBus("RELOAD_ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 750, IType.SIN_FULL).addPos(0, 0, 60, 500).addPos(0, 0, 0, 750, IType.SIN_FULL));
		}
		return null;
	};

}
