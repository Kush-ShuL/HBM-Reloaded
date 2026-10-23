package com.hbm.weapon.sedna;

import com.hbm.block.falling.LegacyFallingRules;
import com.hbm.damage.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.explosion.ExplosionDeathEffects;
import com.hbm.registry.HbmSounds;
import java.util.*;
import java.util.function.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Original MK4 ballistic configuration and standard ricochet/entity-hit callbacks. */
public class BulletConfig {
    public com.hbm.weapon.sedna.gun.GunAmmo ammo;
    public net.minecraft.world.item.ItemStack casingItem;
    public int casingAmount,ammoReloadCount=1;
    public com.hbm.weapon.sedna.gun.particle.SpentCasing casing;
    public BiConsumer<EntityBulletBaseMK4,Float> renderer;
    public BulletConfig setItem(com.hbm.weapon.sedna.gun.GunAmmo ammo){this.ammo=ammo;return this;}
    public BulletConfig setBlackPowder(boolean value){blackPowder=value;return this;}
    public BulletConfig setWear(float value){wear=value;return this;}
    public BulletConfig setProjectiles(int min,int max){projectilesMin=min;projectilesMax=max;return this;}
    public BulletConfig setProjectiles(int amount){return setProjectiles(amount,amount);}
    public BulletConfig setDamageFalloffByPen(boolean value){damageFalloffByPen=value;return this;}
    public BulletConfig setReloadCount(int value){ammoReloadCount=value;return this;}
    public BulletConfig setRenderer(BiConsumer<EntityBulletBaseMK4,Float> value){renderer=value;return this;}
    public BulletConfig setCasing(com.hbm.weapon.sedna.gun.particle.SpentCasing value){casing=value;return this;}
    public BulletConfig setCasing(net.minecraft.world.item.ItemStack value,int count){casingItem=value;casingAmount=count;return this;}
    public static final List<BulletConfig> configs=new ArrayList<>();
    private static final Map<ResourceLocation,BulletConfig> BY_NAME=new LinkedHashMap<>();
    public final ResourceLocation identifier;public final int id;
    public enum ProjectileType { BULLET,BULLET_CHUNKLOADING,BEAM }
    public ProjectileType pType=ProjectileType.BULLET;
    public BiConsumer<EntityBulletBeamBase,HitResult> onImpactBeam;
    public BiConsumer<EntityBulletBeamBase,Float> rendererBeam;
    public static final BiConsumer<EntityBulletBeamBase,HitResult> LAMBDA_STANDARD_BEAM_HIT=BulletConfig::standardBeamHit;
    public static final BiConsumer<EntityBulletBeamBase,HitResult> LAMBDA_BEAM_HIT=BulletConfig::beamHit;
    public float velocity=10,spread=0,wear=1,damageMult=1,armorThresholdNegation=0,armorPiercingPercent=0,knockbackMult=.1F,headshotMult=1.25F,ricochetAngle=5;
    public int projectilesMin=1,projectilesMax=1,maxRicochetCount=2,expires=30,selfDamageDelay=2;
    public boolean damageFalloffByPen=true,impactsEntities=true,doesPenetrate=false,isSpectral=false,blackPowder=false,renderRotations=true,clientKeepsRotation;
    public double gravity;
    public DamageResistanceHandler.DamageClass dmgClass=DamageResistanceHandler.DamageClass.PHYSICAL;
    public Consumer<Entity> onUpdate;
    public BiConsumer<EntityBulletBaseMK4,HitResult> onImpact,onRicochet=BulletConfig::standardRicochet,onEntityHit=BulletConfig::standardEntityHit;
    public BulletConfig(String name){identifier=ResourceLocation.parse(name);if(BY_NAME.putIfAbsent(identifier,this)!=null)throw new IllegalArgumentException("Duplicate bullet "+identifier);id=configs.size();configs.add(this);}
    public static BulletConfig byName(String name){ResourceLocation key=ResourceLocation.tryParse(name);return key==null?null:BY_NAME.get(key);}
    public BulletConfig setBeam(){pType=ProjectileType.BEAM;return this;}
    public BulletConfig setRenderRotations(boolean value){renderRotations=value;return this;}
    public BulletConfig setOnBeamImpact(BiConsumer<EntityBulletBeamBase,HitResult> callback){onImpactBeam=callback;return this;}
    public BulletConfig setRendererBeam(BiConsumer<EntityBulletBeamBase,Float> callback){rendererBeam=callback;return this;}
    public BulletConfig setVel(float value){velocity=value;return this;}public BulletConfig setSpread(float value){spread=value;return this;}public BulletConfig setDamage(float value){damageMult=value;return this;}
    public BulletConfig setThresholdNegation(float value){armorThresholdNegation=value;return this;}public BulletConfig setArmorPiercing(float value){armorPiercingPercent=value;return this;}public BulletConfig setKnockback(float value){knockbackMult=value;return this;}public BulletConfig setHeadshot(float value){headshotMult=value;return this;}
    public BulletConfig setupDamageClass(DamageResistanceHandler.DamageClass value){dmgClass=value;return this;}public BulletConfig setRicochetAngle(float value){ricochetAngle=value;return this;}public BulletConfig setRicochetCount(int value){maxRicochetCount=value;return this;}
    public BulletConfig setGrav(double value){gravity=value;return this;}public BulletConfig setLife(int value){expires=value;return this;}public BulletConfig setImpactsEntities(boolean value){impactsEntities=value;return this;}public BulletConfig setDoesPenetrate(boolean value){doesPenetrate=value;return this;}public BulletConfig setSpectral(boolean value){isSpectral=value;return this;}public BulletConfig setSelfDamageDelay(int value){selfDamageDelay=value;return this;}
    public BulletConfig setOnUpdate(Consumer<Entity> value){onUpdate=value;return this;}public BulletConfig setOnImpact(BiConsumer<EntityBulletBaseMK4,HitResult> value){onImpact=value;return this;}public BulletConfig setOnRicochet(BiConsumer<EntityBulletBaseMK4,HitResult> value){onRicochet=value;return this;}public BulletConfig setOnEntityHit(BiConsumer<EntityBulletBaseMK4,HitResult> value){onEntityHit=value;return this;}
    public static DamageSource getDamage(Entity projectile,LivingEntity shooter,DamageResistanceHandler.DamageClass type){return SednaDamageSource.create(projectile.level(),projectile,shooter,type);}
    public static void standardRicochet(EntityBulletBaseMK4 bullet,HitResult impact){
        if(!(impact instanceof BlockHitResult hit))return;Level level=bullet.level();var position=hit.getBlockPos();var state=level.getBlockState(position);
        if(LegacyFallingRules.material(state).name().equals("glass")){level.destroyBlock(position,false);bullet.setPos(hit.getLocation());return;}
        if(state.getBlock() instanceof ShotSensitiveBlock sensitive)sensitive.onShot(level,position);
        Vec3 face=Vec3.atLowerCornerOf(hit.getDirection().getNormal()),velocity=bullet.getDeltaMovement().normalize();
        double angle=Math.abs(Math.acos(Math.max(-1,Math.min(1,velocity.dot(face))))*180/Math.PI-90);
        if(angle<=bullet.config.ricochetAngle){
            bullet.ricochets++;if(bullet.ricochets>bullet.config.maxRicochetCount){bullet.setPos(hit.getLocation());bullet.setDead();}
            var motion=bullet.getDeltaMovement();bullet.setDeltaMovement(switch(hit.getDirection().getAxis()){case X->motion.multiply(-1,1,1);case Y->motion.multiply(1,-1,1);case Z->motion.multiply(1,1,-1);});
            level.playSound(null,bullet.getX(),bullet.getY(),bullet.getZ(),HbmSounds.get("weapon.ricochet"),SoundSource.NEUTRAL,.25F,1);bullet.setPos(hit.getLocation());bullet.forceNetworkTeleport();
        }else{bullet.setPos(hit.getLocation());bullet.setDead();}
    }
    public static void standardEntityHit(EntityBulletBaseMK4 bullet,HitResult impact){
        if(!(impact instanceof EntityHitResult hit))return;Entity entity=hit.getEntity();
        if(entity==bullet.getThrower()&&bullet.tickCount<bullet.selfDamageDelay())return;if(entity instanceof LivingEntity living&&living.getHealth()<=0)return;
        DamageSource source=getDamage(bullet,bullet.getThrower(),bullet.config.dmgClass);float intended=bullet.damage;
        if(!(entity instanceof LivingEntity living)){LegacyEntityDamage.attackEntityFromIgnoreIFrame(entity,source,bullet.damage);return;}
        if(bullet.config.headshotMult>1){double head=living.getBbHeight()-living.getEyeHeight();if(living.isAlive()&&hit.getLocation().y>living.getY()+living.getBbHeight()-head*2)intended*=bullet.config.headshotMult;}
        float previous=living.getHealth();LegacyEntityDamage.attackEntityFromNT(living,source,intended,true,true,bullet.config.knockbackMult,bullet.config.armorThresholdNegation,bullet.config.armorPiercingPercent);
        if(bullet.config.damageFalloffByPen)bullet.damage-=Math.max(previous-living.getHealth(),0)*.5;
        if(!bullet.doesPenetrate()||bullet.damage<0){bullet.setPos(hit.getLocation());bullet.setDead();}
        if(!living.isAlive())ExplosionDeathEffects.decide(living,source);
    }
    /** Original standard beam callback, without MK4 headshots or penetration damage falloff. */
    public static void standardBeamHit(EntityBulletBeamBase beam,HitResult impact){damageBeam(beam,impact,true,true);}
    /** Original LAMBDA_BEAM_HIT skips special cancellation and the confetti callback. */
    public static void beamHit(EntityBulletBeamBase beam,HitResult impact){damageBeam(beam,impact,false,false);}
    private static void damageBeam(EntityBulletBeamBase beam,HitResult impact,boolean allowSpecialCancel,boolean confetti){
        if(!(impact instanceof EntityHitResult hit))return;var entity=hit.getEntity();
        if(entity instanceof LivingEntity living&&living.getHealth()<=0)return;
        DamageSource source=getDamage(beam,beam.getThrower(),beam.config.dmgClass);
        if(!(entity instanceof LivingEntity living)){LegacyEntityDamage.attackEntityFromIgnoreIFrame(entity,source,beam.damage);return;}
        LegacyEntityDamage.attackEntityFromNT(living,source,beam.damage,true,allowSpecialCancel,beam.config.knockbackMult,beam.config.armorThresholdNegation,beam.config.armorPiercingPercent);
        if(confetti&&!living.isAlive())ExplosionDeathEffects.decide(living,source);
    }

}
