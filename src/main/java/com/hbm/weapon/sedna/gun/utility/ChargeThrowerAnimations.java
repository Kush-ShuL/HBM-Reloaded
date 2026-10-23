package com.hbm.weapon.sedna.gun.utility;
import java.util.function.BiFunction;
import net.minecraft.world.item.ItemStack;
import com.hbm.weapon.sedna.gun.GunAnimation;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
/** Original EQUIP, CYCLE, RELOAD and INSPECT timelines, in source milliseconds. */
public final class ChargeThrowerAnimations {
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> ANIMATIONS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case RELOAD: return new BusAnimation()
				.addBus("RAISE", new BusAnimationSequence().addPos(-45, 0, 0, 500, IType.SIN_FULL).hold(2000).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("AMMO", new BusAnimationSequence().setPos(0, -10, -5).hold(500).addPos(0, 0, 5, 750, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_UP).hold(4000))
				.addBus("TWIST", new BusAnimationSequence().setPos(0, 0, 25).hold(2000).addPos(0, 0, 0, 150));
		case INSPECT: return new BusAnimation()
				.addBus("TURN", new BusAnimationSequence().addPos(0, 60, 0, 500, IType.SIN_FULL).hold(1750).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("ROLL", new BusAnimationSequence().hold(750).addPos(0, 0, -90, 500, IType.SIN_FULL).hold(1000).addPos(0, 0, 0, 500, IType.SIN_FULL));
		}

		return null;
	};
 private ChargeThrowerAnimations(){}
}
