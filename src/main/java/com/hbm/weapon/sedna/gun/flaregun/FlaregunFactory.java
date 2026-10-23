package com.hbm.weapon.sedna.gun.flaregun;
import java.util.function.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.Lego;
import com.hbm.weapon.sedna.gun.mags.MagazineSingleReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
public final class FlaregunFactory {
 private FlaregunFactory(){}
 public static ItemGunBaseNT create(){return new ItemGunBaseNT(WeaponQuality.A_SIDE,new GunConfig()
  .dura(100).draw(7).inspect(39).crosshair(Crosshair.L_CIRCUMFLEX)
  .smoke((stack,ctx)->Lego.handleStandardSmoke(ctx.entity,stack,1500,.025D,1.05D,0))
  .rec(new Receiver(0).dmg(FlaregunSettings.DAMAGE).delay(20).reload(28).jam(33)
   .sound("hbm:weapon.hkShoot",1F,1F).mag(new MagazineSingleReload(0,1).addConfigs(FlaregunBullets.FLARE,FlaregunBullets.SUPPLY,FlaregunBullets.WEAPON))
   .offset(.75,-.0625,-.1875D).setupStandardFire()
   .recoil((stack,ctx)->ItemGunBaseNT.setupRecoil(10,(float)(ctx.getPlayer().getRandom().nextGaussian()*1.5))))
  .setupStandardConfiguration().anim(LAMBDA_FLAREGUN_ANIMS).orchestra(FlaregunOrchestra.SOUNDS))
  .setDefaultAmmo(PistolAmmo.G26_FLARE,3).pistolDualWield(true);}
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_FLAREGUN_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-90, 0, 0, 0).addPos(0, 0, 0, 350, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250))
				.addBus("HAMMER", new BusAnimationSequence().addPos(15, 0, 0, 50).addPos(15, 0, 0, 550).addPos(0, 0, 0, 100));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("HAMMER", new BusAnimationSequence().addPos(15, 0, 0, 50).addPos(15, 0, 0, 550).addPos(0, 0, 0, 100));
		case RELOAD: return new BusAnimation()
				.addBus("OPEN", new BusAnimationSequence().addPos(45, 0, 0, 200, IType.SIN_FULL).addPos(45, 0, 0, 750).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("SHELL", new BusAnimationSequence().addPos(4, -8, -4, 0).addPos(4, -8, -4, 200).addPos(0, 0, -5, 500, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("FLIP", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(25, 0, 0, 200, IType.SIN_DOWN).addPos(25, 0, 0, 800).addPos(0, 0, 0, 200, IType.SIN_DOWN));
		case JAMMED: return new BusAnimation()
				.addBus("OPEN", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(45, 0, 0, 200, IType.SIN_FULL).addPos(45, 0, 0, 500).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("FLIP", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 200).addPos(25, 0, 0, 200, IType.SIN_DOWN).addPos(25, 0, 0, 550).addPos(0, 0, 0, 200, IType.SIN_DOWN));
		case INSPECT: return new BusAnimation()
				.addBus("FLIP", new BusAnimationSequence().addPos(-360 * 3, 0, 0, 1500, IType.SIN_FULL));
		}

		return null;
	};

}
