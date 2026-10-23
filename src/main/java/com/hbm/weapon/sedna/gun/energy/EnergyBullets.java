package com.hbm.weapon.sedna.gun.energy;

import com.hbm.bomb.conventional.grenade.*;
import com.hbm.bomb.nuclear.NuclearPlasmaBlastEffects;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import java.util.*;
import java.util.function.BiConsumer;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

/** All ten XFactoryEnergy configs; their true hit callbacks reuse the migrated SEDNA damage/fire paths. */
public final class EnergyBullets {
    public static GunAmmo ammo(String name){return GunAmmo.standard(switch(name){case "CAPACITOR"->67;case "CAPACITOR_OVERCHARGE"->68;case "CAPACITOR_IR"->69;default->throw new IllegalArgumentException(name);});}
    public static final BiConsumer<EntityBulletBeamBase,HitResult> LIGHTNING=(beam,hit)->{
        var position=hit.getLocation();
        if(hit instanceof BlockHitResult block)position=position.add(net.minecraft.world.phys.Vec3.atLowerCornerOf(block.getDirection().getNormal()).scale(.5));
        var explosion=new ExplosionVNT(beam.level(),position.x,position.y,position.z,2F,beam.getThrower());
        explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1,beam.damage).setDamageClass(beam.config.dmgClass));
        explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.explode();
        beam.level().playSound(null,position.x,position.y,position.z,com.hbm.registry.HbmSounds.get("entity.ufoBlast"),net.minecraft.sounds.SoundSource.NEUTRAL,5F,.9F+beam.level().random.nextFloat()*.2F);
        beam.level().playSound(null,position.x,position.y,position.z,net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_BLAST,net.minecraft.sounds.SoundSource.NEUTRAL,5F,.5F);
        float yaw=beam.level().random.nextFloat()*180F;
        for(int i=0;i<3;i++)NuclearPlasmaBlastEffects.send(beam.level(),position.x,position.y,position.z,.5F,.5F,1F,2F,-60F+60F*i,yaw,100);
        if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof LivingEntity living){living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60,9));living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,60,9));}
    };
    public static final BiConsumer<EntityBulletBeamBase,HitResult> SPLIT=(beam,hit)->{
        LIGHTNING.accept(beam,hit);if(!(hit instanceof EntityHitResult struck))return;
        var origin=hit.getLocation();var targets=beam.level().getEntitiesOfClass(LivingEntity.class,new AABB(origin,origin).inflate(20));Collections.shuffle(targets);
        for(var target:targets){if(target==beam.thrower||target==struck.getEntity())continue;
            var delta=new Vec3(target.getX()-origin.x,target.getY()+target.getBbHeight()/2-origin.y,target.getZ()-origin.z);if(delta.length()>20)continue;
            var sub=new EntityBulletBeamBase(beam.thrower,EnergyBullets.energy_tesla_ir_sub,beam.damage);sub.setPos(origin);sub.setRotationsFromVector(delta);sub.performHitscanExternal(delta.length());beam.level().addFreshEntity(sub);
        }
    };
    public static final BiConsumer<EntityBulletBeamBase,HitResult> INFRARED=(beam,hit)->{
        BulletConfig.LAMBDA_STANDARD_BEAM_HIT.accept(beam,hit);
        if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof LivingEntity living){var data=FireStatus.data(living);if(data.fire<100)data.fire=100;}
        if(hit instanceof BlockHitResult block){var level=beam.level();var pos=block.getBlockPos();var side=block.getDirection();var state=level.getBlockState(pos);
            if(state.isFlammable(level,pos,side.getOpposite())&&level.isEmptyBlock(pos.relative(side))){level.setBlockAndUpdate(pos.relative(side),Blocks.FIRE.defaultBlockState());return;}
            var fire=new LingeringFire(level).area(2,1).duration(100).type(0);fire.setPos(hit.getLocation());level.addFreshEntity(fire);
        }
    };
    private static BulletConfig base(String name,int meta,DamageClass damage){return new BulletConfig("hbm:energy/"+name).setItem(GunAmmo.standard(meta)).setupDamageClass(damage).setBeam().setSpread(0F).setLife(5).setRenderRotations(false);}
    public static final BulletConfig energy_tesla=base("tesla",67,DamageClass.ELECTRIC).setDoesPenetrate(true).setOnBeamImpact(LIGHTNING);
    public static final BulletConfig energy_tesla_overcharge=base("tesla_overcharge",68,DamageClass.ELECTRIC).setDoesPenetrate(true).setDamage(1.5F).setOnBeamImpact(LIGHTNING);
    public static final BulletConfig energy_tesla_ir=base("tesla_ir",69,DamageClass.ELECTRIC).setDamage(.8F).setOnBeamImpact(SPLIT);
    public static final BulletConfig energy_tesla_ir_sub=base("tesla_ir_sub",69,DamageClass.ELECTRIC).setLife(3).setWear(3F).setDoesPenetrate(true).setDamage(.5F).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
    public static final BulletConfig energy_las=base("las",67,DamageClass.LASER).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
    public static final BulletConfig energy_las_overcharge=base("las_overcharge",68,DamageClass.LASER).setDoesPenetrate(true).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
    public static final BulletConfig energy_las_ir=base("las_ir",69,DamageClass.FIRE).setOnBeamImpact(INFRARED);
    public static final BulletConfig energy_emerald=base("emerald",67,DamageClass.LASER).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT).setArmorPiercing(.5F).setThresholdNegation(50F);
    public static final BulletConfig energy_emerald_overcharge=base("emerald_overcharge",68,DamageClass.LASER).setDoesPenetrate(true).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT).setArmorPiercing(.5F).setThresholdNegation(75F);
    public static final BulletConfig energy_emerald_ir=base("emerald_ir",69,DamageClass.FIRE).setOnBeamImpact(INFRARED).setArmorPiercing(.5F).setThresholdNegation(50F);
    public static final List<BulletConfig> ALL=List.of(energy_tesla,energy_tesla_overcharge,energy_tesla_ir,energy_tesla_ir_sub,energy_las,energy_las_overcharge,energy_las_ir,energy_emerald,energy_emerald_overcharge,energy_emerald_ir);
    public static void init(){}
    public static void bindCasings(){for(var config:ALL)if(config!=energy_tesla_ir_sub)config.setCasing(new ItemStack(HbmContent.item("ingot_polymer"),2),4);}
    private EnergyBullets(){}
}
