package com.hbm.weapon.sedna.gun.factory;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
public class Lego {

	public static final Random ANIM_RAND = new Random();

	/**
	 * If IDLE and the mag of receiver 0 can be loaded, set state to RELOADING. Used by keybinds. */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_RELOAD = (stack, ctx) -> {

		Player player = ctx.getPlayer();
		Receiver rec = ctx.config.getReceivers(stack)[0];
		GunState state = ItemGunBaseNT.getState(stack, ctx.configIndex);

		if(state == GunState.IDLE) {

			ItemGunBaseNT.setIsAiming(stack, false);
			IMagazine mag = rec.getMagazine(stack);

			if(mag.canReload(stack, ctx.inventory)) {
				int loaded = mag.getAmount(stack, ctx.inventory);
				mag.setAmountBeforeReload(stack, loaded);
				ItemGunBaseNT.setState(stack, ctx.configIndex, GunState.RELOADING);
				ItemGunBaseNT.setTimer(stack, ctx.configIndex, rec.getReloadBeginDuration(stack) + (loaded <= 0 ? rec.getReloadCockOnEmptyPre(stack) : 0));
				ItemGunBaseNT.playAnimation(player, stack, GunAnimation.RELOAD, ctx.configIndex);
				if(ctx.config.getReloadChangesType(stack)) mag.initNewType(stack, ctx.inventory);
			} else {
				ItemGunBaseNT.playAnimation(player, stack, GunAnimation.INSPECT, ctx.configIndex);
				if(!ctx.config.getInspectCancel(stack)) {
					ItemGunBaseNT.setState(stack, ctx.configIndex, GunState.DRAWING);
					ItemGunBaseNT.setTimer(stack, ctx.configIndex, ctx.config.getInspectDuration(stack));
				}
			}
		}
	};

	/** If IDLE and ammo is loaded, fire and set to JUST_FIRED. */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_CLICK_PRIMARY = (stack, ctx) -> { clickReceiver(stack, ctx, 0); };

	public static void clickReceiver(ItemStack stack, LambdaContext ctx, int receiver) {

		LivingEntity entity = ctx.entity;
		Player player = ctx.getPlayer();
		Receiver rec = ctx.config.getReceivers(stack)[receiver];
		int index = ctx.configIndex;
		GunState state = ItemGunBaseNT.getState(stack, index);

		if(state == GunState.IDLE) {

			if(rec.getCanFire(stack).apply(stack, ctx)) {
				rec.getOnFire(stack).accept(stack, ctx);

				if(rec.getFireSound(stack) != null)
					GunSounds.play(entity, rec.getFireSound(stack), rec.getFireVolume(stack), rec.getFirePitch(stack));

				int remaining = rec.getRoundsPerCycle(stack) - 1;
				for(int i = 0; i < remaining; i++) if(rec.getCanFire(stack).apply(stack, ctx)) rec.getOnFire(stack).accept(stack, ctx);

				ItemGunBaseNT.setState(stack, index, GunState.COOLDOWN);
				ItemGunBaseNT.setTimer(stack, index, rec.getDelayAfterFire(stack));
			} else {

				if(rec.getDoesDryFire(stack)) {
					ItemGunBaseNT.playAnimation(player, stack, GunAnimation.CYCLE_DRY, index);
					ItemGunBaseNT.setState(stack, index, rec.getRefireAfterDry(stack) ? GunState.COOLDOWN : GunState.DRAWING);
					ItemGunBaseNT.setTimer(stack, index, rec.getDelayAfterDryFire(stack));
				}
			}
		}

		if(state == GunState.RELOADING) {
			ItemGunBaseNT.setReloadCancel(stack, true);
		}
	}

	/** If IDLE, switch mode between 0 and 1. */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_CLICK_SECONDARY = (stack, ctx) -> {

		LivingEntity entity = ctx.entity;
		int index = ctx.configIndex;
		GunState state = ItemGunBaseNT.getState(stack, index);

		if(state == GunState.IDLE) {
			int mode = ItemGunBaseNT.getMode(stack, 0);
			ItemGunBaseNT.setMode(stack, index, 1 - mode);
			if(mode == 0)
				GunSounds.play(entity, "hbm:weapon.switchmode1", 1F, 1F);
			else
				GunSounds.play(entity, "hbm:weapon.switchmode2", 1F, 1F);
		}
	};

	/** Default smoke. */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_SMOKE = (stack, ctx) -> {
		handleStandardSmoke(ctx.entity, stack, 2000, 0.025D, 1.15D, ctx.configIndex);
	};

	public static void handleStandardSmoke(LivingEntity entity, ItemStack stack, int smokeDuration, double alphaDecay, double widthGrowth, int index) {
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		long lastShot = gun.lastShot[index];
		List<SmokeNode> smokeNodes = gun.getConfig(stack, index).smokeNodes;

		boolean smoking = lastShot + smokeDuration > System.currentTimeMillis();
		if(!smoking && !smokeNodes.isEmpty()) smokeNodes.clear();

		if(smoking) {
			LegacyVector prev = LegacyVector.createVectorHelper(-entity.getDeltaMovement().x, -entity.getDeltaMovement().y, -entity.getDeltaMovement().z);
			prev.rotateAroundY((float) (entity.getYRot() * Math.PI / 180D));
			double accel = 15D;
			double side = (entity.getYRot() - entity.yHeadRotO) * 0.1D;
			double waggle = 0.025D;

			for(SmokeNode node : smokeNodes) {
				node.forward += -prev.zCoord * accel + entity.level().random.nextGaussian() * waggle;
				node.lift += prev.yCoord + 1.5D;
				node.side += prev.xCoord * accel + entity.level().random.nextGaussian() * waggle + side;
				if(node.alpha > 0) node.alpha -= alphaDecay;
				node.width *= widthGrowth;
			}

			double alpha = (System.currentTimeMillis() - lastShot) / (double) smokeDuration;
			alpha = (1 - alpha) * 0.5D;

			if(gun.getState(stack, index) == GunState.RELOADING || smokeNodes.size() == 0) alpha = 0;
			smokeNodes.add(new SmokeNode(alpha));
		}
	}

	/** Toggles isAiming. Used by keybinds. */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_TOGGLE_AIM = (stack, ctx) -> { ItemGunBaseNT.setIsAiming(stack, !ItemGunBaseNT.getIsAiming(stack)); };

	/** Returns true if the mag has ammo in it. Used by keybind functions on whether to fire, and deciders on whether to trigger a refire. */
	public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_STANDARD_CAN_FIRE = (stack, ctx) -> { return ctx.config.getReceivers(stack)[0].getMagazine(stack).getAmount(stack, ctx.inventory) > 0; };
	public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_SECOND_CAN_FIRE = (stack, ctx) -> { return ctx.config.getReceivers(stack)[1].getMagazine(stack).getAmount(stack, ctx.inventory) > 0; };

	/** Returns true if the mag has ammo in it, and the gun is in the locked on state */
	public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_LOCKON_CAN_FIRE = (stack, ctx) -> { return ctx.config.getReceivers(stack)[0].getMagazine(stack).getAmount(stack, ctx.inventory) > 0 && ItemGunBaseNT.getIsLockedOn(stack); };




	/** JUMPER - bypasses mag testing and just allows constant fire */
	public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_DEBUG_CAN_FIRE = (stack, ctx) -> { return true; };

	/** Spawns an EntityBulletBaseMK4 with the loaded bulletcfg */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STANDARD_FIRE = (stack, ctx) -> {
		doStandardFire(stack, ctx, GunAnimation.CYCLE, 0, true);
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SECOND_FIRE = (stack, ctx) -> {
		doStandardFire(stack, ctx, GunAnimation.CYCLE, 1, true);
	};
	/** Spawns an EntityBulletBaseMK4 with the loaded bulletcfg, ignores wear */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_NOWEAR_FIRE = (stack, ctx) -> {
		doStandardFire(stack, ctx, GunAnimation.CYCLE, 0, false);
	};
	/** Spawns an EntityBulletBaseMK4 with the loaded bulletcfg, then resets lockon progress */
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_LOCKON_FIRE = (stack, ctx) -> {
		doStandardFire(stack, ctx, GunAnimation.CYCLE, 0, true);
		ItemGunBaseNT.setIsLockedOn(stack, false);
	};

	public static void doStandardFire(ItemStack stack, LambdaContext ctx, GunAnimation anim, int receiver, boolean calcWear) {
		LivingEntity entity = ctx.entity;
		Player player = ctx.getPlayer();
		int index = ctx.configIndex;
		if(anim != null) ItemGunBaseNT.playAnimation(player, stack, anim, ctx.configIndex);

		boolean aim = ItemGunBaseNT.getIsAiming(stack);
		Receiver primary = ctx.config.getReceivers(stack)[receiver];
		IMagazine mag = primary.getMagazine(stack);
		BulletConfig config = (BulletConfig) mag.getType(stack, ctx.inventory);

		LegacyVector offset = ItemGunBaseNT.getIsAiming(stack) ? primary.getProjectileOffsetScoped(stack) : primary.getProjectileOffset(stack);
		double forwardOffset = offset.xCoord;
		double heightOffset = offset.yCoord;
		double sideOffset = offset.zCoord;
        if(player!=null&&(GunHands.hand(player,stack)==net.minecraft.world.InteractionHand.MAIN_HAND?player.getMainArm():player.getMainArm().getOpposite())==net.minecraft.world.entity.HumanoidArm.LEFT)sideOffset=-sideOffset;

		/*forwardOffset = 0.75;
		heightOffset = -0.125;
		sideOffset = -0.25D;*/

		int projectiles = config.projectilesMin;
		if(config.projectilesMax > config.projectilesMin) projectiles += entity.getRandom().nextInt(config.projectilesMax - config.projectilesMin + 1);
		projectiles = (int) (projectiles * primary.getSplitProjectiles(stack));

		for(int i = 0; i < projectiles; i++) {
			float damage = calcDamage(ctx, stack, primary, calcWear, index);
			float spread = calcSpread(ctx, stack, primary, config, calcWear, index, aim);

			var projectile = GunProjectiles.create(entity, config, damage, spread, sideOffset, heightOffset, forwardOffset);
            if (projectile instanceof EntityBulletBaseMK4 mk4) {
                if (ItemGunBaseNT.getIsLockedOn(stack)) mk4.lockonTarget = entity.level().getEntity(ItemGunBaseNT.getLockonTarget(stack));
                if (i == 0 && config.blackPowder) GunNetwork.blackPowder(entity.level(), mk4.getX(), mk4.getY(), mk4.getZ(), mk4.getDeltaMovement().x, mk4.getDeltaMovement().y, mk4.getDeltaMovement().z, 10, .25F, .5F, 10, .25F);
            }
            entity.level().addFreshEntity(projectile);
		}

		if(player != null) GunStats.bulletFired(player);
		mag.useUpAmmo(stack, ctx.inventory, 1);
		if(calcWear) ItemGunBaseNT.setWear(stack, index, Math.min(ItemGunBaseNT.getWear(stack, index) + config.wear, ctx.config.getDurability(stack)));
	}

	public static float getStandardWearSpread(ItemStack stack, GunConfig config, int index) {
		float percent = (float) ItemGunBaseNT.getWear(stack, index) / config.getDurability(stack);
		if(percent < 0.5F) return 0F;
		return (percent - 0.5F) * 2F;
	}

	/** Returns the standard multiplier for damage based on wear */
	public static float getStandardWearDamage(ItemStack stack, GunConfig config, int index) {
		float percent = (float) ItemGunBaseNT.getWear(stack, index) / config.getDurability(stack);
		if(percent < 0.75F) return 1F;
		return 1F - (percent - 0.75F) * 2F;
	}

	/** Returns the full calculated damage based on guncfg and wear */
	public static float calcDamage(LambdaContext ctx, ItemStack stack, Receiver primary, boolean calcWear, int index) {
		return primary.getBaseDamage(stack) * (calcWear ? getStandardWearDamage(stack, ctx.config, index) : 1);
	}

	public static float calcSpread(LambdaContext ctx, ItemStack stack, Receiver primary, BulletConfig config, boolean calcWear, int index, boolean aim) {
		// the gun's innate spread, SMGs will have poor accuracy no matter what
		float spreadInnate = primary.getInnateSpread(stack);
		// the ammo's spread (for example for buckshot) multiplied with the gun's ammo modifier (choke or sawed off barrel)
		float spreadAmmo = config.spread * primary.getAmmoSpread(stack);
		// hipfire penalty, i.e. extra spread when not aiming
		float spreadHipfire = aim ? 0F : primary.getHipfireSpread(stack);
		// extra spread caused by weapon durability, [0;0.125] by default
		float spreadWear = !calcWear ? 0F : (getStandardWearSpread(stack, ctx.config, index) * primary.getDurabilitySpread(stack));

		return spreadInnate + spreadAmmo + spreadHipfire + spreadWear;
	}

	public static void standardExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range) { standardExplode(bullet, mop, range, 1F); }
	public static void standardExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range, float damageMod) {
		ExplosionVNT vnt = new ExplosionVNT(bullet.level(), mop.getLocation().x, mop.getLocation().y, mop.getLocation().z, range, bullet.getThrower());
		vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.damage * damageMod).setupPiercing(bullet.config.armorThresholdNegation, bullet.config.armorPiercingPercent));
		vnt.setPlayerProcessor(new PlayerProcessorStandard());
		vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
		vnt.explode();
	}

	public static void tinyExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range) { tinyExplode(bullet, mop, range, 1F); }
	public static void tinyExplode(EntityBulletBaseMK4 bullet, HitResult mop, float range, float damageMod) {
		var dir = mop instanceof BlockHitResult block ? net.minecraft.world.phys.Vec3.atLowerCornerOf(block.getDirection().getNormal()) : net.minecraft.world.phys.Vec3.ZERO;
		double x = mop.getLocation().x + dir.x * 0.25D;
		double y = mop.getLocation().y + dir.y * 0.25D;
		double z = mop.getLocation().z + dir.z * 0.25D;
		ExplosionVNT vnt = new ExplosionVNT(bullet.level(), x, y, z, range, bullet.getThrower());
		vnt.setEntityProcessor(new EntityProcessorCrossSmooth(0.5, bullet.damage * damageMod)
				.setupPiercing(bullet.config.armorThresholdNegation, bullet.config.armorPiercingPercent).setKnockback(0.25D));
		vnt.setPlayerProcessor(new PlayerProcessorStandard());
		vnt.setSFX(new ExplosionEffectTiny());
		vnt.explode();
	}

}
