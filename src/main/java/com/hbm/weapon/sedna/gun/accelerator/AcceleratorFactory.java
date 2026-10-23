package com.hbm.weapon.sedna.gun.accelerator;
import java.util.function.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.entity.projectile.*;
import com.hbm.bomb.nuclear.LegacyVector;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
/** Actual original factory and full animation buses, with explicit modern world/coin bridges. */
public class AcceleratorFactory {

	public static java.util.function.Supplier<Float> GUN_TAU_CANNON_DAMAGE=AcceleratorSettings.value("GUN_TAU_CANNON_DAMAGE",25F);
	public static java.util.function.Supplier<Float> GUN_TAU_CANNON_CHARGE_DAMAGE=AcceleratorSettings.value("GUN_TAU_CANNON_CHARGE_DAMAGE",5F);
	public static java.util.function.Supplier<Float> GUN_COILGUN_DAMAGE=AcceleratorSettings.value("GUN_COILGUN_DAMAGE",35F);
	public static java.util.function.Supplier<Float> GUN_NI4NI_DAMAGE=AcceleratorSettings.value("GUN_NI4NI_DAMAGE",35F);

	public static MagazineBelt tauChargeMag = new MagazineBelt();

	public static BulletConfig tau_uranium;
	public static BulletConfig tau_uranium_charge;

	public static BulletConfig coil_tungsten;
	public static BulletConfig coil_ferrouranium;

	public static BulletConfig ni4ni_arc;
	
	public static void initConfig() {}

	public static Consumer<Entity> LAMBDA_UPDATE_TUNGSTEN = (entity) -> {breakInPath(entity, 1.25F); };
	public static Consumer<Entity> LAMBDA_UPDATE_FERRO = (entity) -> { breakInPath(entity, 2.5F); };

    public static void breakInPath(Entity entity,float threshold){
        LegacyVector delta=LegacyVector.createVectorHelper(entity.getX()-entity.xo,entity.getY()-entity.yo,entity.getZ()-entity.zo);
        double motion=Math.max(delta.lengthVector(),.1D);LegacyVector direction=delta.normalize();
        for(double d=0;d<motion;d+=.5D){double x=entity.getX()-direction.xCoord*d,y=entity.getY()-direction.yCoord*d,z=entity.getZ()-direction.zCoord*d;
            if(entity.level().isClientSide)com.hbm.client.weapon.accelerator.CoilFireworkTrail.spawn(x,y,z);
            else{var pos=net.minecraft.core.BlockPos.containing(x,y,z);var state=entity.level().getBlockState(pos);float hardness=state.getDestroySpeed(entity.level(),pos);if(!state.isAir()&&hardness>=0&&hardness<threshold)entity.level().destroyBlock(pos,false);}
        }
    }

	public static void init() {

		tau_uranium = new BulletConfig("hbm:accelerator/tau_uranium").setItem(PistolAmmo.TAU_URANIUM).setupDamageClass(DamageClass.SUBATOMIC).setBeam().setLife(5).setRenderRotations(false).setDoesPenetrate(true).setDamageFalloffByPen(false)
				.setOnBeamImpact(BulletConfig.LAMBDA_BEAM_HIT);
		tau_uranium_charge = new BulletConfig("hbm:accelerator/tau_uranium_charge").setItem(PistolAmmo.TAU_URANIUM).setupDamageClass(DamageClass.SUBATOMIC).setBeam().setLife(5).setRenderRotations(false).setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true)
				.setOnBeamImpact(BulletConfig.LAMBDA_BEAM_HIT);

		coil_tungsten = new BulletConfig("hbm:accelerator/coil_tungsten").setItem(PistolAmmo.COIL_TUNGSTEN).setVel(7.5F).setLife(50).setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true)
				.setOnUpdate(LAMBDA_UPDATE_TUNGSTEN);
		coil_ferrouranium = new BulletConfig("hbm:accelerator/coil_ferrouranium").setItem(PistolAmmo.COIL_FERROURANIUM).setVel(7.5F).setLife(50).setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true)
				.setOnUpdate(LAMBDA_UPDATE_FERRO);

		ni4ni_arc = new BulletConfig("hbm:accelerator/ni4ni_arc").setupDamageClass(DamageClass.PHYSICAL).setBeam().setLife(5).setThresholdNegation(10F).setArmorPiercing(0.2F).setRenderRotations(false).setDoesPenetrate(false)
				.setOnBeamImpact(BulletConfig.LAMBDA_BEAM_HIT);

		tauChargeMag.addConfigs(tau_uranium_charge);

		AcceleratorGuns.gun_tau = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(6_400).draw(10).inspect(10).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
						.dmg(GUN_TAU_CANNON_DAMAGE).spreadHipfire(0F).delay(4).auto(true).spread(0F)
						.mag(new MagazineBelt().addConfigs(tau_uranium))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_TAU))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY)
				.rp(LAMBDA_TAU_PRIMARY_RELEASE)
				.ps(LAMBDA_TAU_SECONDARY_PRESS)
				.rs(LAMBDA_TAU_SECONDARY_RELEASE)
				.pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_TAU_ANIMS).orchestra(AcceleratorOrchestras.ORCHESTRA_TAU)
				).setDefaultAmmo(PistolAmmo.TAU_URANIUM, 15);

		AcceleratorGuns.gun_coilgun = new ItemGunBaseNT(WeaponQuality.SPECIAL, new GunConfig()
				.dura(400).draw(5).inspect(39).crosshair(Crosshair.L_CIRCUMFLEX)
				.rec(new Receiver(0)
						.dmg(GUN_COILGUN_DAMAGE).delay(5).reload(20).jam(33).sound("hbm:weapon.coilgunShoot", 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 1).addConfigs(coil_tungsten, coil_ferrouranium))
						.offset(0.75, -0.0625, -0.1875D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_COILGUN))
				.setupStandardConfiguration()
				.anim(LAMBDA_COILGUN_ANIMS).orchestra(AcceleratorOrchestras.ORCHESTRA_COILGUN)
				).setDefaultAmmo(PistolAmmo.COIL_TUNGSTEN, 5);

		AcceleratorGuns.gun_n_i_4_n_i = new ItemGunNI4NI(WeaponQuality.SPECIAL, new GunConfig()
				.dura(0).draw(5).inspect(39).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
						.dmg(GUN_NI4NI_DAMAGE).delay(10).sound("hbm:weapon.coilgunShoot", 1.0F, 1.0F)
						.mag(new MagazineInfinite(ni4ni_arc))
						.offset(0.75, -0.0625, -0.1875D)
						.setupStandardFire().fire(Lego.LAMBDA_NOWEAR_FIRE))
				.setupStandardConfiguration()
				.ps(LAMBDA_NI4NI_SECONDARY_PRESS)
				.anim(LAMBDA_NI4NI_ANIMS).orchestra(AcceleratorOrchestras.ORCHESTRA_COILGUN)
				).pistolDualWield(true);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_TAU_PRIMARY_RELEASE = (stack, ctx) -> {
		if(ctx.getPlayer() == null || ItemGunBaseNT.getLastAnim(stack, ctx.configIndex) != GunAnimation.CYCLE) return;
		GunSounds.play(ctx.getPlayer(),"hbm:weapon.fire.tauRelease",1F,1F);
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_TAU_SECONDARY_PRESS = (stack, ctx) -> {
		if(ctx.getPlayer() == null) return;
		if(ctx.config.getReceivers(stack)[0].getMagazine(stack).getAmount(stack, ctx.inventory) <= 0) return;
		ItemGunBaseNT.playAnimation(ctx.getPlayer(), stack, GunAnimation.SPINUP, ctx.configIndex);
		tauChargeMag.getMagType(stack); //caches the last loaded ammo
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_TAU_SECONDARY_RELEASE = (stack, ctx) -> {
		if(ctx.getPlayer() == null) return;
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(timer >= 10 && ItemGunBaseNT.getLastAnim(stack, ctx.configIndex) == GunAnimation.SPINUP) {
			ItemGunBaseNT.playAnimation(ctx.getPlayer(), stack, GunAnimation.ALT_CYCLE, ctx.configIndex);
			int unitsUsed = 1 + Math.min(12, timer / 10);

			LivingEntity entity = ctx.entity;
			int index = ctx.configIndex;

			Receiver primary = ctx.config.getReceivers(stack)[0];
			BulletConfig config = tauChargeMag.getFirstConfig(stack, ctx.inventory);

			LegacyVector offset = primary.getProjectileOffset(stack);
			double forwardOffset = offset.xCoord;
			double heightOffset = offset.yCoord;
			double sideOffset = offset.zCoord;

			float damage = Lego.getStandardWearDamage(stack, ctx.config, index) * unitsUsed * GUN_TAU_CANNON_CHARGE_DAMAGE.get();
			float spread = Lego.calcSpread(ctx, stack, primary, config, true, index, false);
			EntityBulletBeamBase mk4 = new EntityBulletBeamBase(entity, config, damage, spread, sideOffset, heightOffset, forwardOffset);
			entity.level().addFreshEntity(mk4);

			ItemGunBaseNT.setWear(stack, index, Math.min(ItemGunBaseNT.getWear(stack, index) + config.wear * unitsUsed, ctx.config.getDurability(stack)));

		} else {
			ItemGunBaseNT.playAnimation(ctx.getPlayer(), stack, GunAnimation.CYCLE_DRY, ctx.configIndex);
		}
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_NI4NI_SECONDARY_PRESS = (stack, ctx) -> {
		if(ctx.getPlayer() == null) return;
		Player player = ctx.getPlayer();

		if(ItemGunNI4NI.getCoinCount(stack) > 0) {
            Vec3 velocity=player.getLookAngle().scale(.8D);
            EntityCoin coin=new EntityCoin(player.level());
            coin.setPos(player.getX(),player.getY()+player.getEyeHeight()-EntityCoin.LEGACY_Y_OFFSET-.125,player.getZ());
            coin.setDeltaMovement(velocity.x,velocity.y+.5,velocity.z);coin.setYRot(player.getYRot());coin.setThrower(player);player.level().addFreshEntity(coin);
            GunSounds.play(player,"random.orb",1F,1F+player.getRandom().nextFloat()*.25F);

			ItemGunNI4NI.setCoinCount(stack, ItemGunNI4NI.getCoinCount(stack) - 1);
		}
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_TAU = (stack, ctx) -> { };

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_COILGUN = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_TAU_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -5, 50, IType.SIN_DOWN).addPos(0, 0, 5, 100, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
		case ALT_CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -3, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -5, 50, IType.SIN_DOWN).addPos(0, 0, 5, 100, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
		case CYCLE_DRY: return new BusAnimation();
		case INSPECT: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(2, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -360 * 3, 500 * 3, IType.SIN_DOWN));
		case SPINUP: return new BusAnimation()
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 360 * 6, 3000, IType.SIN_UP).addPos(0, 0, 0, 0).addPos(0, 0, 360 * 40, 500 * 20));
		}

		return null;
	};

	public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_COILGUN_ANIMS = (stack, type) -> {
		if(type == GunAnimation.EQUIP) return new BusAnimation().addBus("RELOAD", new BusAnimationSequence().addPos(1, 0, 0, 0).addPos(0, 0, 0, 250));
		if(type == GunAnimation.CYCLE) return new BusAnimation().addBus("RECOIL", new BusAnimationSequence().addPos(ItemGunBaseNT.getIsAiming(stack) ? 0.5 : 1, 0, 0, 100).addPos(0, 0, 0, 200));
		if(type == GunAnimation.RELOAD) return new BusAnimation().addBus("RELOAD", new BusAnimationSequence().addPos(1, 0, 0, 250).addPos(1, 0, 0, 500).addPos(0, 0, 0, 250));
		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_NI4NI_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-360 * 2, 0, 0, 500));
		case CYCLE:
			boolean aiming = ItemGunBaseNT.getIsAiming(stack);
			return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(aiming ? -5 : -30, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("DRUM", new BusAnimationSequence().hold(50).addPos(0, 0, 120, 300, IType.SIN_FULL));
		case INSPECT: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-360 * 3, 0, 0, 750).hold(100).addPos(0, 0, 0, 750));
		}

		return null;
	};
}
