package com.hbm.weapon.sedna.gun.fatman;
import java.util.function.BiFunction;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.weapon.sedna.gun.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
/** Every actual EQUIP/CYCLE/RELOAD/JAMMED/INSPECT source bus and duration. */
public final class FatmanAnimations {
 private static final RandomSource FALLBACK=RandomSource.create();
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> ANIMATIONS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
		case CYCLE:
			var player = ItemGunBaseNT.CLIENT.localPlayer();
			var rand = player == null ? FALLBACK : player.getRandom();
			return new BusAnimation()
				.addBus("GAUGE", new BusAnimationSequence().addPos(0, 0, 135 + rand.nextInt(136), 100, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_DOWN))
				.addBus("PISTON", new BusAnimationSequence().addPos(0, 0, 3, 100, IType.SIN_UP))
				.addBus("NUKE", new BusAnimationSequence().addPos(0, 0, 3, 100, IType.SIN_UP).addPos(0, 0, 0, 0));
		case RELOAD: return new BusAnimation()
				.addBus("LID", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -45, 250, IType.SIN_UP).addPos(0, 0, -45, 1200).addPos(0, 0, 0, 250, IType.SIN_UP))
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, -2, 500, IType.SIN_FULL).addPos(0, 0, -2, 1700).addPos(0, 0, 0, 750, IType.SIN_FULL))
				.addBus("NUKE", new BusAnimationSequence().addPos(5, -4, 3, 0).addPos(5, -4, 3, 750).addPos(2, 0.5, 3, 500, IType.SIN_UP).addPos(1, 0.5, 3, 100).addPos(0, 0, 3, 100).addPos(0, 0, 3, 750).addPos(0, 0, 0, 750, IType.SIN_FULL))
				.addBus("PISTON", new BusAnimationSequence().addPos(0, 0, 3, 0).addPos(0, 0, 3, 2200).addPos(0, 0, 0, 750, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().addPos(5, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 450).addPos(3, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL).addPos(0, 0, 0, 500).addPos(-10, 0, 0, 375, IType.SIN_DOWN).addPos(0, 0, 0, 375, IType.SIN_UP));
		case JAMMED: return new BusAnimation()
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-15, 0, 0, 250, IType.SIN_FULL).addPos(-15, 0, 0, 1000).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case INSPECT: return new BusAnimation()
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().addPos(-15, 0, 0, 250, IType.SIN_FULL).addPos(-15, 0, 0, 1000).addPos(0, 0, 0, 250, IType.SIN_FULL));
		}
		return null;
	};

 private FatmanAnimations(){}
}
