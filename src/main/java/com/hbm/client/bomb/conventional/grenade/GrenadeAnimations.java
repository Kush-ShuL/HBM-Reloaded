package com.hbm.client.bomb.conventional.grenade;
import com.hbm.bomb.conventional.grenade.*;
import com.hbm.client.animation.AnimationClock;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
/** The four source equip buses, running on the shared original hotbar animation clock. */
public final class GrenadeAnimations {
    private GrenadeAnimations(){}
    public static void equip(){var player=Minecraft.getInstance().player;if(player==null)return;var stack=player.getMainHandItem();if(!(stack.getItem() instanceof UniversalGrenadeItem))return;HbmAnimations.hotbar[player.getInventory().selected][0]=new HbmAnimations.Animation(stack.getItem().getDescriptionId(),AnimationClock.get_ms(),animation(stack));}
    public static BusAnimation animation(ItemStack stack){
		GrenadeParts.Shell shell = UniversalGrenadeItem.shell(stack);
		
		if(shell == GrenadeParts.Shell.FRAG) {
			return new BusAnimation()
					.addBus("BODYMOVE", new BusAnimationSequence().setPos(0, -5, 0).addPos(0, -3, 0, 350).addPos(0, 0, 0, 350, IType.SIN_DOWN))
					.addBus("BODYTURN", new BusAnimationSequence().addPos(0, 0, 45, 350).addPos(0, 0, -15, 350, IType.SIN_DOWN).hold(200).addPos(0, 0, -20, 100, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL))
					.addBus("RINGMOVE", new BusAnimationSequence().hold(900).addPos(0, 0, 1, 150).addPos(0, -3, 3, 300))
					.addBus("RINGTURN", new BusAnimationSequence().hold(900).addPos(0, 0, 45, 300))
					.addBus("RENDERRING", new BusAnimationSequence().setPos(1, 1, 1).hold(1350).setPos(0, 0, 0));
		}
		
		if(shell == GrenadeParts.Shell.STICK) {
			return new BusAnimation()
					.addBus("BODYMOVE", new BusAnimationSequence().setPos(0, -7, 0).addPos(0, 3, 0, 750, IType.SIN_DOWN).holdUntil(1900).addPos(0, 0, 0, 250, IType.SIN_FULL))
					.addBus("BODYTURN", new BusAnimationSequence().setPos(0, 0, 90).addPos(0, 0, -45, 750, IType.SIN_DOWN).holdUntil(1900).addPos(0, 0, 0, 250, IType.SIN_FULL))
					.addBus("RINGMOVE", new BusAnimationSequence().hold(800).addPos(0, -0.25, 0, 200, IType.SIN_FULL).hold(250).addPos(0, -0.5, 0, 200, IType.SIN_FULL).addPos(2, -5, 0, 350, IType.SIN_UP))
					.addBus("RINGTURN", new BusAnimationSequence().hold(800).addPos(0, 360, 0, 200, IType.SIN_FULL).hold(250).addPos(0, 360 * 2, 0, 200, IType.SIN_FULL))
					.addBus("RENDERRING", new BusAnimationSequence().setPos(1, 1, 1).hold(2100).setPos(0, 0, 0));
		}
		
		if(shell == GrenadeParts.Shell.TECH) {
			return new BusAnimation()
					.addBus("BODYMOVE", new BusAnimationSequence().setPos(0, -5, 0).addPos(0, -3, 0, 350).addPos(0, 0, 0, 350, IType.SIN_DOWN))
					.addBus("BODYTURN", new BusAnimationSequence().addPos(0, 0, 45, 350).addPos(0, 0, -15, 350, IType.SIN_DOWN).hold(200).addPos(0, 0, -20, 100, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL))
					.addBus("RINGMOVE", new BusAnimationSequence().hold(900).addPos(0, 0, 1, 150).addPos(0, -3, 3, 300))
					.addBus("RINGTURN", new BusAnimationSequence().hold(900).addPos(0, 0, 45, 300))
					.addBus("RENDERRING", new BusAnimationSequence().setPos(1, 1, 1).hold(1350).setPos(0, 0, 0));
		}
		
		if(shell == GrenadeParts.Shell.NUKE) {
			return new BusAnimation()
					.addBus("BODYMOVE", new BusAnimationSequence().setPos(0, -5, 0).hold(250).addPos(0, 0, 0, 850, IType.SIN_DOWN))
					.addBus("BODYTURN", new BusAnimationSequence().setPos(0, 0, 90).hold(250).addPos(0, 0, -25, 850, IType.SIN_DOWN).hold(200).addPos(0, 0, -30, 100, IType.SIN_DOWN).addPos(0, 0, 0, 750, IType.SIN_FULL))
					.addBus("RINGMOVE", new BusAnimationSequence().hold(1300).addPos(0, 0, 1, 150).addPos(0, -3, 3, 300))
					.addBus("RINGTURN", new BusAnimationSequence().hold(1300).addPos(0, 0, 720, 500)) // SPEEN
					.addBus("RENDERRING", new BusAnimationSequence().setPos(1, 1, 1).hold(1750).setPos(0, 0, 0));
		}
		
		return null;
    }
}
