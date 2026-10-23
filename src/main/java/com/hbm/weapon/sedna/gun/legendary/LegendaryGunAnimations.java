package com.hbm.weapon.sedna.gun.legendary;
import java.util.function.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.ItemStack;
/** Exact original bus keys, timings and fallback branches. */
public final class LegendaryGunAnimations {
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_NOPIP_ANIMS = (stack, type) -> {
		switch(type) {
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250))
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 400).addPos(0, 0, 0, 200))
				.addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, 1, 200));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 300 + 100).addPos(0, 0, 0, 200))
				.addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, 1, 200));
		case EQUIP: return new BusAnimation().addBus("ROTATE", new BusAnimationSequence().addPos(90, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case RELOAD: return new BusAnimation()
					.addBus("RELAOD_TILT", new BusAnimationSequence().addPos(-15, 0, 0, 100).addPos(65, 0, 0, 100).addPos(45, 0, 0, 50).addPos(0, 0, 0, 200).addPos(0, 0, 0, 1450).addPos(-80, 0, 0, 100).addPos(-80, 0, 0, 100).addPos(0, 0, 0, 200))
					.addBus("RELOAD_CYLINDER", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(90, 0, 0, 100).addPos(90, 0, 0, 1700).addPos(0, 0, 0, 70))
					.addBus("RELOAD_LIFT", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(-45, 0, 0, 250).addPos(-45, 0, 0, 350).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 1050).addPos(0, 0, 0, 100))
					.addBus("RELOAD_JOLT", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(2, 0, 0, 50).addPos(0, 0, 0, 100))
					.addBus("RELOAD_BULLETS", new BusAnimationSequence().addPos(0, 0, 0, 650).addPos(10, 0, 0, 300).addPos(10, 0, 0, 200).addPos(0, 0, 0, 700))
					.addBus("RELOAD_BULLETS_CON", new BusAnimationSequence().addPos(1, 0, 0, 0).addPos(1, 0, 0, 950).addPos(0, 0, 0, 1 ) );
		case INSPECT:
		case JAMMED: return new BusAnimation()
					.addBus("RELAOD_TILT", new BusAnimationSequence().addPos(-15, 0, 0, 100).addPos(65, 0, 0, 100).addPos(45, 0, 0, 50).addPos(0, 0, 0, 200).addPos(0, 0, 0, 200).addPos(-80, 0, 0, 100).addPos(-80, 0, 0, 100).addPos(0, 0, 0, 200))
					.addBus("RELOAD_CYLINDER", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(90, 0, 0, 100).addPos(90, 0, 0, 450).addPos(0, 0, 0, 70));
		}

		return null;
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_LILMAC_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation().addBus("SPIN", new BusAnimationSequence().addPos(-360, 0, 0, 350));
		}

		return LAMBDA_NOPIP_ANIMS.apply(stack, type);
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MARESLEG_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(0, 0, -3, 500, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -1, 50).addPos(0, 0, 0, 250))
				.addBus("SIGHT", new BusAnimationSequence().addPos(35, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-85, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200));
		case RELOAD:
			boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer()))) <= 0;
			return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(30, 0, 0, 400, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 400).addPos(-85, 0, 0, 200))
				.addBus("SHELL", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0.25, -3, 0).addPos(0, empty ? 0.25 : 0.125, -1.5, 150, IType.SIN_UP).addPos(0, empty ? 0.25 : -0.25, 0, 150, IType.SIN_DOWN))
				.addBus("FLAG", new BusAnimationSequence().addPos(0, 0, 0, empty ? 900 : 0).addPos(1, 1, 1, 0));
		case RELOAD_CYCLE: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(30, 0, 0, 0))
				.addBus("LEVER", new BusAnimationSequence().addPos(-85, 0, 0, 0))
				.addBus("SHELL", new BusAnimationSequence().addPos(0, 0.25, -3, 0).addPos(0, 0.125, -1.5, 150, IType.SIN_UP).addPos(0, -0.125, 0, 150, IType.SIN_DOWN))
				.addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
		case RELOAD_END: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(30, 0, 0, 0).addPos(30, 0, 0, 250).addPos(0, 0, 0, 400, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(-85, 0, 0, 0).addPos(0, 0, 0, 200))
				.addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(30, 0, 0, 0).addPos(30, 0, 0, 250).addPos(0, 0, 0, 400, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(-85, 0, 0, 0).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 650).addPos(-85, 0, 0, 200).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 200).addPos(-85, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 850).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 45, 800).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
		case INSPECT: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-35, 0, 0, 300, IType.SIN_FULL).addPos(-35, 0, 0, 1150).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, -90, 500, IType.SIN_FULL).addPos(0, 0, -90, 500).addPos(0, 0, 0, 500, IType.SIN_FULL));
		}

		return null;
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MARESLEG_SHORT_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(0, 0, -3, 250, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -1, 50).addPos(0, 0, 0, 250))
				.addBus("SIGHT", new BusAnimationSequence().addPos(35, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-85, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200))
				.addBus("FLIP", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(360, 0, 0, 400))
				.addBus("SHELL", new BusAnimationSequence().addPos(-20, 0, 0, 0)); //gets rid of the shell in the barrel during cycling
		case CYCLE_DRY: return new BusAnimation()
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200))
				.addBus("FLIP", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(360, 0, 0, 400))
				.addBus("SHELL", new BusAnimationSequence().addPos(-20, 0, 0, 0));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(30, 0, 0, 0).addPos(30, 0, 0, 250).addPos(0, 0, 0, 400, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(-85, 0, 0, 0).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 650).addPos(-85, 0, 0, 200).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 200).addPos(-85, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("FLAG", new BusAnimationSequence().addPos(1, 1, 1, 0));
		}

		return LAMBDA_MARESLEG_ANIMS.apply(stack, type);
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_SEXY_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
		case CYCLE:
			int amount = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null);
			return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().hold(50).addPos(0, 0, -0.25, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("BARREL", new BusAnimationSequence().addPos(0, 0, -1, 50, IType.SIN_DOWN).addPos(0, 0, 0, 150))
				.addBus("CYCLE", new BusAnimationSequence().addPos(1, 0, 0, 150))
				.addBus("HOOD", new BusAnimationSequence().hold(50).addPos(3, 0, 0, 50, IType.SIN_DOWN).addPos(0, 0, 0, 50, IType.SIN_UP))
				.addBus("SHELLS", new BusAnimationSequence().setPos(amount - 1, 0, 0));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("CYCLE", new BusAnimationSequence().addPos(0, 0, 18, 50));
		case RELOAD: return new BusAnimation()
				.addBus("LOWER", new BusAnimationSequence().addPos(15, 0, 0, 500, IType.SIN_FULL).hold(2750).addPos(12, 0, 0, 100, IType.SIN_DOWN).addPos(15, 0, 0, 100, IType.SIN_FULL).hold(1050).addPos(18, 0, 0, 100, IType.SIN_DOWN).addPos(15, 0, 0, 100, IType.SIN_FULL).hold(300).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 1, 150).hold(4700).addPos(0, 0, 0, 150))
				.addBus("HOOD", new BusAnimationSequence().hold(250).addPos(60, 0, 0, 500, IType.SIN_FULL).hold(3250).addPos(0, 0, 0, 500, IType.SIN_UP))
				.addBus("BELT", new BusAnimationSequence().setPos(1, 0, 0).hold(750).addPos(0, 0, 0, 500, IType.SIN_UP).hold(2000).addPos(1, 0, 0, 500, IType.SIN_UP))
				.addBus("MAG", new BusAnimationSequence().hold(1500).addPos(0, -1, 0, 250, IType.SIN_UP).addPos(2, -1, 0, 500, IType.SIN_UP).addPos(7, 1, 0, 250, IType.SIN_UP).addPos(15, 2, 0, 250).setPos(0, -2, 0).addPos(0, 0, 0, 500, IType.SIN_UP))
				.addBus("MAGROT", new BusAnimationSequence().hold(2250).addPos(0, 0, -180, 500, IType.SIN_FULL).setPos(0, 0, 0));
		case INSPECT: return new BusAnimation()
				.addBus("BOTTLE", new BusAnimationSequence().setPos(8, -8, -2).addPos(6, -4, -2, 500, IType.SIN_DOWN).addPos(3, -3, -5, 500, IType.SIN_FULL).addPos(3, -2, -5, 1000).addPos(4, -6, -2, 750, IType.SIN_FULL).addPos(6, -8, -2, 500, IType.SIN_UP))
				.addBus("SIP", new BusAnimationSequence().setPos(25, 0, 0).hold(500).addPos(-90, 0, 0, 500, IType.SIN_FULL).addPos(-110, 0, 0, 1000).addPos(25, 0, 0, 750, IType.SIN_FULL));
		}

		return null;
	};
}
