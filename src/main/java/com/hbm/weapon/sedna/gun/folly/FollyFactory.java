package com.hbm.weapon.sedna.gun.folly;
import com.hbm.bomb.nuclear.*;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.entity.projectile.*;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.Lego;
import com.hbm.weapon.sedna.gun.mags.MagazineSingleReload;
import com.hbm.client.animation.bus.BusAnimation;
import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import java.util.List;
import java.util.function.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
public final class FollyFactory {
	
	// no config here lol
	public static final java.util.function.Supplier<Float> GUN_FOLLY_DAMAGE = ()->1_000F;

	public static BulletConfig folly_sm;
	public static BulletConfig folly_nuke;

    public static Consumer<Entity> LAMBDA_SM_UPDATE=entity->{
        if(entity.level().isClientSide)return;
        var beam=(EntityBulletBeamBase)entity;
        // Original Vec3NT.normalizeSelf uses the source float-rounded square-root.
        var delta=new Vec3(beam.headingX,beam.headingY,beam.headingZ);
        double length=(double)(float)Math.sqrt(delta.lengthSqr());
        var dir=length<1.0E-4D?Vec3.ZERO:delta.scale(1D/length);
        if(beam.tickCount<50){double spacing=10,dist=beam.tickCount*spacing;
            NuclearPlasmaBlastEffects.sendAtOrigin(beam.level(),beam.getX()+dir.x*dist,beam.getY()+dir.y*dist,beam.getZ()+dir.z*dist,.75F,.75F,.75F,2F+beam.tickCount/(float)(beam.beamLength/spacing)*3F,beam.getXRot()+90,-beam.getYRot(),250,beam.getX(),beam.getY(),beam.getZ());}
        if(beam.tickCount!=2)return;
        if(beam.thrower!=null)HbmExposure.contaminate(beam.thrower,HbmExposure.Contamination.CREATIVE,150F);
        List<Entity> entities=beam.level().getEntities(beam,beam.getBoundingBox().expandTowards(beam.headingX,beam.headingY,beam.headingZ).inflate(1));
        for(int i=1;i<beam.beamLength;i+=2){int x=Mth.floor(beam.getX()+dir.x*i),y=Mth.floor(beam.getY()+dir.y*i),z=Mth.floor(beam.getZ()+dir.z*i);
            for(int ix=x-1;ix<=x+1;ix++)for(int iy=y-1;iy<=y+1;iy++)for(int iz=z-1;iz<=z+1;iz++){
                if(iy>beam.level().getMinBuildHeight()&&iy<beam.level().getMaxBuildHeight())beam.level().setBlock(new BlockPos(ix,iy,iz),Blocks.AIR.defaultBlockState(),3);
                var bounds=new AABB(ix-1,iy-1,iz-1,ix+2,iy+2,iz+2);
                for(var hit:entities)if(hit!=beam.thrower&&hit.getBoundingBox().intersects(bounds)){
                    var damage=BulletConfig.getDamage(beam,beam.thrower,beam.config.dmgClass);
                    if(hit instanceof LivingEntity living)LegacyEntityDamage.attackEntityFromNT(living,damage,beam.damage,true,false,0,1000F,1F);
                    else LegacyEntityDamage.attackEntityFromIgnoreIFrame(hit,damage,beam.damage);
                }
            }
        }
    };
    public static BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_NUKE_IMPACT=(bullet,hit)->{
        if(hit.getType()==HitResult.Type.ENTITY&&bullet.tickCount<2)return;
        if(bullet.isRemoved())return;bullet.setDead();var location=hit.getLocation();
        bullet.level().addFreshEntity(NuclearExplosionMK5.statFac(bullet.level(),100,location.x,location.y,location.z));
        NuclearTorex.statFacStandard(bullet.level(),location.x,location.y,location.z,100);
    };

	public static void init() {

		folly_sm = new BulletConfig("hbm:folly/sm").setItem(GunAmmo.secret(0)).setupDamageClass(DamageClass.SUBATOMIC).setBeam().setLife(100).setVel(2F).setGrav(0.015D).setRenderRotations(false).setSpectral(true).setDoesPenetrate(true)
				.setOnUpdate(LAMBDA_SM_UPDATE);
		folly_nuke = new BulletConfig("hbm:folly/nuke").setItem(GunAmmo.secret(1)).setLife(600).setVel(4F).setGrav(0.015D)
				.setOnImpact(LAMBDA_NUKE_IMPACT);
        folly_nuke.pType=BulletConfig.ProjectileType.BULLET_CHUNKLOADING;

	}

	public static ItemGunBaseNT gun_folly(){
		return new ItemGunBaseNT(WeaponQuality.SECRET, new GunConfig()
				.dura(0).draw(40).crosshair(Crosshair.NONE)
				.rec(new Receiver(0)
						.dmg(GUN_FOLLY_DAMAGE).delay(26).dryfire(false).reload(160).jam(0).sound("hbm:weapon.fire.loudestNoiseOnEarth", 100.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 1).addConfigs(folly_sm, folly_nuke))
						.offset(0.75, -0.0625, -0.1875D).offsetScoped(0.75, -0.0625, -0.125D)
						.canFire(LAMBDA_CAN_FIRE).fire(LAMBDA_FIRE).recoil(LAMBDA_RECOIL_FOLLY))
				.setupStandardConfiguration().pt(LAMBDA_TOGGLE_AIM)
				.anim(LAMBDA_FOLLY_ANIMS).orchestra(ORCHESTRA_FOLLY)
				);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_TOGGLE_AIM = (stack, ctx) -> {
		if(ItemGunBaseNT.getState(stack, ctx.configIndex) == GunState.IDLE) {
			boolean wasAiming = ItemGunBaseNT.getIsAiming(stack);
			ItemGunBaseNT.setIsAiming(stack, !wasAiming);
			if(!wasAiming) ItemGunBaseNT.playAnimation(ctx.getPlayer(), stack, GunAnimation.SPINUP, ctx.configIndex);
		}
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_FIRE = (stack, ctx) -> {
		Lego.doStandardFire(stack, ctx, GunAnimation.CYCLE, 0, false);
	};

	public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_CAN_FIRE = (stack, ctx) -> {
		if(ctx.entity instanceof Player) {
			if(!ItemGunBaseNT.getIsAiming(stack)) return false;
			if(ItemGunBaseNT.getLastAnim(stack, ctx.configIndex) != GunAnimation.SPINUP) return false;
			if(ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex) < 100) return false;
		}
		return ctx.config.getReceivers(stack)[0].getMagazine(stack).getAmount(stack, ctx.inventory) > 0;
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_FOLLY = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(25, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_FOLLY_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(5, 0, 0, 1500, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -4.5, 50).addPos(0, 0, -4.5, 500).addPos(0, 0, 0, 500, IType.SIN_UP))
				.addBus("LOAD", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(-25, 0, 0, 250, IType.SIN_DOWN).addPos(0, 0, 0, 1000, IType.SIN_FULL));
		case RELOAD: return new BusAnimation()
				.addBus("LOAD", new BusAnimationSequence().addPos(60, 0, 0, 1000, IType.SIN_FULL).addPos(60, 0, 0, 6000).addPos(0, 0, 0, 1000, IType.SIN_FULL))
				.addBus("SCREW", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -135, 1000, IType.SIN_FULL).addPos(0, 0, -135, 4000).addPos(0, 0, 0, 1000, IType.SIN_FULL))
				.addBus("BREECH", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -0.5, 1000, IType.SIN_FULL).addPos(0, -4, -0.5, 1000, IType.SIN_FULL).addPos(0, -4, -0.5, 2000).addPos(0, 0, -0.5, 1000, IType.SIN_FULL).addPos(0, 0, 0, 1000, IType.SIN_FULL))
				.addBus("SHELL", new BusAnimationSequence().addPos(0, -4, -4.5, 0).addPos(0, -4, -4.5, 3000).addPos(0, 0, -4.5, 1000, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_UP));
		}

		return null;
	};
    public static final BiConsumer<ItemStack,LambdaContext> ORCHESTRA_FOLLY=(stack,context)->{
        if(context.entity.level().isClientSide)return;
        if(ItemGunBaseNT.getLastAnim(stack,context.configIndex)==GunAnimation.RELOAD){int timer=ItemGunBaseNT.getAnimTimer(stack,context.configIndex);
            if(timer==20||timer==120)GunSounds.play(context.entity,"hbm:weapon.reload.screw",1,1);
            if(timer==80)GunSounds.play(context.entity,"hbm:weapon.reload.insertRocket",1,1);
        }
    };
}
