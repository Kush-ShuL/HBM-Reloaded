package com.hbm.weapon.sedna.gun.factory;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import com.hbm.client.animation.bus.BusAnimation;
import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;

/** X5846 XFactoryBlackPowder's complete Pepperbox, retaining its four real bullets and every animation bus/keyframe. */
public final class PepperboxFactory {
    public static final BulletConfig stone=new BulletConfig("hbm:black_powder/stone").setItem(GunAmmo.standard(0)).setBlackPowder(true).setHeadshot(1F).setSpread(.025F).setRicochetAngle(15);
    public static final BulletConfig flint=new BulletConfig("hbm:black_powder/flint").setItem(GunAmmo.standard(1)).setBlackPowder(true).setHeadshot(1F).setSpread(.01F).setRicochetAngle(5).setDoesPenetrate(true).setDamage(1.5F);
    public static final BulletConfig iron=new BulletConfig("hbm:black_powder/iron").setItem(GunAmmo.standard(2)).setBlackPowder(true).setHeadshot(1F).setSpread(0F).setRicochetAngle(90).setRicochetCount(5).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.5F);
    public static final BulletConfig shot=new BulletConfig("hbm:black_powder/shot").setItem(GunAmmo.standard(3)).setBlackPowder(true).setHeadshot(1F).setSpread(.1F).setRicochetAngle(45).setProjectiles(6,6).setDamage(1F/6F);
    public static void initBullets() { }
    public static ItemGunBaseNT create() {
        return new ItemGunBaseNT(WeaponQuality.A_SIDE,new GunConfig()
            .dura(300).draw(4).inspect(23).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
            .rec(new Receiver(0).dmg(GunSettings::pepperboxDamage).delay(27).reload(67).jam(58)
                .sound("hbm:weapon.fire.blackPowder",1F,1F).mag(new MagazineFullReload(0,6).addConfigs(stone,flint,iron,shot))
                .offset(.75,-.0625,-.1875D).setupStandardFire().recoil(LAMBDA_RECOIL_PEPPERBOX))
            .setupStandardConfiguration().anim(LAMBDA_PEPPERBOX_ANIMS).orchestra(PepperboxOrchestra.PEPPERBOX))
            .pistolDualWield(true).setDefaultAmmo(GunAmmo.standard(0),12);
    }
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_PEPPERBOX = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_PEPPERBOX_ANIMS = (stack, type) -> {
		switch(type) {
		case CYCLE: return new BusAnimation()
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 1025).addPos(60, 0, 0, 250))
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(45, 0, 0, 150, IType.SIN_DOWN).addPos(45, 0, 0, 50).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("HAMMER", new BusAnimationSequence().addPos(80, 0, 0, 25).addPos(80, 0, 0, 1000).addPos(0, 0, 0, 250))
				.addBus("TRIGGER", new BusAnimationSequence().addPos(1, 0, 0, 25).addPos(1, 0, 0, 250).addPos(0, 0, 0, 100));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 525).addPos(60, 0, 0, 250))
				.addBus("HAMMER", new BusAnimationSequence().addPos(80, 0, 0, 25).addPos(80, 0, 0, 500).addPos(0, 0, 0, 250))
				.addBus("TRIGGER", new BusAnimationSequence().addPos(1, 0, 0, 25).addPos(1, 0, 0, 250).addPos(0, 0, 0, 100));
		case EQUIP: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(-45, 0, 0, 0).addPos(0, 0, 0, 200, IType.SIN_DOWN));
		case RELOAD: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(90, 0, 0, 500, IType.SIN_FULL).addPos(90, 0, 0, 1600).addPos(0, 0, 0, 500, IType.SIN_FULL).addPos(-5, 0, 0, 200, IType.SIN_UP).addPos(0, 0, 0, 200, IType.SIN_DOWN))
				.addBus("TRANSLATE", new BusAnimationSequence().addPos(0, -12, 5, 500, IType.SIN_FULL).addPos(0, -12, 5, 700).addPos(0, -13, 5, 200).addPos(0, -12, 5, 200).addPos(0, -12, 5, 500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("LOADER", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 5, -5, 0).addPos(0, 0, -0.1, 500, IType.SIN_FULL).addPos(0, 0, -1, 200).addPos(0, 0, -1, 200).addPos(0, 0, -0.1, 200).addPos(0, 5, -5, 500, IType.SIN_FULL).addPos(0, 0, 0, 0))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 2600).addPos(-360 * 1, 0, 0, 750, IType.SIN_FULL))
				.addBus("SHOT", new BusAnimationSequence().addPos(1, 0, 0, 1400).addPos(0, 0, 0, 0));
		case INSPECT: return new BusAnimation()
				.addBus("ROTATE", new BusAnimationSequence().addPos(-360 * 1, 0, 0, 750, IType.SIN_FULL))
				.addBus("RECOIL", new BusAnimationSequence().addPos(-5, 0, 0, 200, IType.SIN_UP).addPos(0, 0, 0, 200, IType.SIN_DOWN));
		case JAMMED: return new BusAnimation()
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 1300).addPos(60, 0, 0, 500, IType.SIN_FULL).addPos(60, 0, 0, 400).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("TRANSLATE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, -6, 0, 400, IType.SIN_FULL).addPos(0, -6, 0, 2000).addPos(0, 0, 0, 400, IType.SIN_FULL))
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(45, 0, 0, 400, IType.SIN_FULL).addPos(45, 0, 0, 2000).addPos(0, 0, 0, 400, IType.SIN_FULL));
		}

		return null;
	};
}
