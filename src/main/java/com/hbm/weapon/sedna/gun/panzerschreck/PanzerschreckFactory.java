package com.hbm.weapon.sedna.gun.panzerschreck;
import java.util.function.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
/** Actual XFactoryRocket RPZB declaration; other source launchers remain separate families. */
public final class PanzerschreckFactory {
 private PanzerschreckFactory(){}
 public static ItemGunBaseNT create(){return new ItemGunBaseNT(WeaponQuality.A_SIDE,new GunConfig()
  .dura(300).draw(7).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX)
  .rec(new Receiver(0).dmg(PanzerschreckSettings.DAMAGE).delay(5).reload(50).jam(40)
   .sound("hbm:weapon.rpgShoot",1F,1F).mag(new MagazineSingleReload(0,1).addConfigs(PanzerschreckBullets.ROCKETS))
   .offset(1,-.0625*1.5,-.1875D).setupStandardFire().recoil((stack,ctx)->{}))
  .setupStandardConfiguration().anim(LAMBDA_PANZERSCHRECK_ANIMS).orchestra((stack,ctx)->{
   var entity=ctx.entity;if(entity.level().isClientSide)return;
   var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
   if(type==GunAnimation.CYCLE && timer==0)GunNetwork.flash(entity);
   if(type==GunAnimation.RELOAD && timer==30)GunSounds.play(entity,"hbm:weapon.reload.insertCanister",1F,1F);
  })).setDefaultAmmo(PistolAmmo.ROCKET_HE,3);}
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_PANZERSCHRECK_ANIMS = (stack, type) -> {
		boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, ItemGunBaseNT.CLIENT.localPlayer() == null ? null : new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())) <= 0;
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case RELOAD: return new BusAnimation()
				.addBus("RELOAD", new BusAnimationSequence().addPos(90, 0, 0, 750, IType.SIN_FULL).addPos(90, 0, 0, 1000).addPos(0, 0, 0, 750, IType.SIN_FULL))
				.addBus("ROCKET", new BusAnimationSequence().addPos(0, -3, -6, 0).addPos(0, -3, -6, 750).addPos(0, 0, -6.5, 500, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_UP));
		case JAMMED: empty = false;
		case INSPECT:
			return new BusAnimation()
				.addBus("RELOAD", new BusAnimationSequence().addPos(90, 0, 0, 750, IType.SIN_FULL).addPos(90, 0, 0, 500).addPos(0, 0, 0, 750, IType.SIN_FULL))
				.addBus("ROCKET", new BusAnimationSequence().addPos(0, empty ? -3 : 0, 0, 0));
		}
		return null;
	};

}
