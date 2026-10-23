package com.hbm.weapon.sedna.gun.flamer;

import com.hbm.bomb.conventional.grenade.FireStatus;
import com.hbm.bomb.conventional.grenade.LingeringFire;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.factory.Lego;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** All fourteen original configs and exact callbacks, sharing the real MK4/FireStatus/LingeringFire/VNT implementations. */
public final class FlamerBullets {
    public static volatile Consumer<Entity> CLIENT_FIRE=entity->{},CLIENT_BALEFIRE=entity->{};
    public static final Consumer<Entity> LAMBDA_FIRE=entity->{if(entity.level().isClientSide)CLIENT_FIRE.accept(entity);};
    public static final Consumer<Entity> LAMBDA_BALEFIRE=entity->{if(entity.level().isClientSide)CLIENT_BALEFIRE.accept(entity);};
    public static final BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_IGNITE_FIRE=(bullet,hit)->{if(hit instanceof EntityHitResult struck&&struck.getEntity()instanceof LivingEntity living){var data=FireStatus.data(living);if(data.fire<100)data.fire=100;}};
    public static final BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_IGNITE_BALEFIRE=(bullet,hit)->{if(hit instanceof EntityHitResult struck&&struck.getEntity()instanceof LivingEntity living){var data=FireStatus.data(living);if(data.balefire<200)data.balefire=200;}};
    public static final BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_LINGER_DIESEL=(bullet,hit)->{if(!igniteIfPossible(bullet,hit))spawnFire(bullet,hit,2,1,100,0);};
    public static final BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_LINGER_GAS=(bullet,hit)->igniteIfPossible(bullet,hit);
    public static final BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_LINGER_NAPALM=(bullet,hit)->{if(!igniteIfPossible(bullet,hit))spawnFire(bullet,hit,2.5F,1,200,0);};
    public static final BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_LINGER_BALEFIRE=(bullet,hit)->spawnFire(bullet,hit,3,1,300,1);

    public static boolean igniteIfPossible(EntityBulletBaseMK4 bullet,HitResult hit){
        if(hit instanceof BlockHitResult block){var level=bullet.level();var position=block.getBlockPos();var side=block.getDirection();var state=level.getBlockState(position);
            if(state.isFlammable(level,position,side.getOpposite())&&level.isEmptyBlock(position.relative(side))){level.setBlock(position.relative(side),Blocks.FIRE.defaultBlockState(),3);return true;}
            // Original successful ignition returns before setDead; do not silently change that projectile quirk.
            bullet.setDead();
        }
        return false;
    }
    public static void spawnFire(EntityBulletBaseMK4 bullet,HitResult hit,float width,float height,int duration,int type){
        if(hit instanceof BlockHitResult){var location=hit.getLocation();
            var fires=bullet.level().getEntitiesOfClass(LingeringFire.class,new AABB(location,location).inflate(width/2+.5,height/2+.5,width/2+.5));
            if(fires.isEmpty()){var fire=new LingeringFire(bullet.level()).area(width,height).duration(duration).type(type);fire.setPos(location);bullet.level().addFreshEntity(fire);}
            bullet.setDead();
        }
    }
    private static BulletConfig base(String name,int metadata,int life,double gravity){return new BulletConfig("hbm:flamer/"+name).setItem(GunAmmo.standard(metadata)).setupDamageClass(DamageClass.FIRE).setLife(life).setVel(1).setGrav(gravity).setReloadCount(500).setSelfDamageDelay(20).setKnockback(0);}
    public static final BulletConfig flame_diesel=base("flame_diesel",63,100,.02).setOnImpact(LAMBDA_IGNITE_FIRE).setOnUpdate(LAMBDA_FIRE).setOnRicochet(LAMBDA_LINGER_DIESEL);
    public static final BulletConfig flame_gas=base("flame_gas",64,10,0).setSpread(.05F).setOnImpact(LAMBDA_IGNITE_FIRE).setOnUpdate(LAMBDA_FIRE).setOnRicochet(LAMBDA_LINGER_GAS);
    public static final BulletConfig flame_napalm=base("flame_napalm",65,200,.02).setOnImpact(LAMBDA_IGNITE_FIRE).setOnUpdate(LAMBDA_FIRE).setOnRicochet(LAMBDA_LINGER_NAPALM);
    public static final BulletConfig flame_balefire=base("flame_balefire",66,200,.02).setOnImpact(LAMBDA_IGNITE_BALEFIRE).setOnUpdate(LAMBDA_BALEFIRE).setOnRicochet(LAMBDA_LINGER_BALEFIRE);

    /** Exact source shallow clone semantics, with an independently registered named config/id and every mutable field retained. */
    private static BulletConfig copy(String name,BulletConfig source){
        var target=new BulletConfig("hbm:flamer/"+name);
        target.ammo=source.ammo;target.casingItem=source.casingItem;target.casingAmount=source.casingAmount;target.ammoReloadCount=source.ammoReloadCount;target.casing=source.casing;target.renderer=source.renderer;
        target.pType=source.pType;target.onImpactBeam=source.onImpactBeam;target.rendererBeam=source.rendererBeam;
        target.velocity=source.velocity;target.spread=source.spread;target.wear=source.wear;target.damageMult=source.damageMult;target.armorThresholdNegation=source.armorThresholdNegation;target.armorPiercingPercent=source.armorPiercingPercent;target.knockbackMult=source.knockbackMult;target.headshotMult=source.headshotMult;target.ricochetAngle=source.ricochetAngle;
        target.projectilesMin=source.projectilesMin;target.projectilesMax=source.projectilesMax;target.maxRicochetCount=source.maxRicochetCount;target.expires=source.expires;target.selfDamageDelay=source.selfDamageDelay;
        target.damageFalloffByPen=source.damageFalloffByPen;target.impactsEntities=source.impactsEntities;target.doesPenetrate=source.doesPenetrate;target.isSpectral=source.isSpectral;target.blackPowder=source.blackPowder;target.renderRotations=source.renderRotations;target.clientKeepsRotation=source.clientKeepsRotation;
        target.gravity=source.gravity;target.dmgClass=source.dmgClass;target.onUpdate=source.onUpdate;target.onImpact=source.onImpact;target.onRicochet=source.onRicochet;target.onEntityHit=source.onEntityHit;return target;
    }
    public static final BulletConfig flame_nograv=copy("flame_nograv",flame_diesel).setGrav(0);
    public static final BulletConfig flame_nograv_bf=copy("flame_nograv_bf",flame_balefire).setGrav(0).setLife(100);
    public static final BulletConfig flame_topaz_diesel=copy("flame_topaz_diesel",flame_diesel).setProjectiles(2).setSpread(.05F).setLife(60).setGrav(0);
    public static final BulletConfig flame_topaz_gas=copy("flame_topaz_gas",flame_gas).setProjectiles(2).setSpread(.05F);
    public static final BulletConfig flame_topaz_napalm=copy("flame_topaz_napalm",flame_napalm).setProjectiles(2).setSpread(.05F).setLife(60).setGrav(0);
    public static final BulletConfig flame_topaz_balefire=copy("flame_topaz_balefire",flame_balefire).setProjectiles(2).setSpread(.05F).setLife(60).setGrav(0);
    public static final BulletConfig flame_daybreaker_diesel=copy("flame_daybreaker_diesel",flame_diesel).setLife(200).setVel(2).setGrav(.035).setOnImpact((bullet,hit)->{Lego.standardExplode(bullet,hit,5);spawnFire(bullet,hit,6,2,200,0);bullet.setDead();});
    public static final BulletConfig flame_daybreaker_gas=copy("flame_daybreaker_gas",flame_gas).setLife(200).setVel(2).setGrav(.035).setOnImpact((bullet,hit)->{Lego.standardExplode(bullet,hit,5);bullet.setDead();});
    public static final BulletConfig flame_daybreaker_napalm=copy("flame_daybreaker_napalm",flame_napalm).setLife(200).setVel(2).setGrav(.035).setOnImpact((bullet,hit)->{Lego.standardExplode(bullet,hit,7.5F);spawnFire(bullet,hit,6,2,300,0);bullet.setDead();});
    public static final BulletConfig flame_daybreaker_balefire=copy("flame_daybreaker_balefire",flame_balefire).setLife(200).setVel(2).setGrav(.035).setOnImpact((bullet,hit)->{Lego.standardExplode(bullet,hit,5);spawnFire(bullet,hit,7.5F,2.5F,400,1);bullet.setDead();});
    public static final List<BulletConfig> ALL=List.of(flame_diesel,flame_gas,flame_napalm,flame_balefire,flame_nograv,flame_nograv_bf,flame_topaz_diesel,flame_topaz_gas,flame_topaz_napalm,flame_topaz_balefire,flame_daybreaker_diesel,flame_daybreaker_gas,flame_daybreaker_napalm,flame_daybreaker_balefire);
    public static void init(){}
    public static void bindCasings(){
        // The four base ItemStacks are distinct; their true clone descendants share each base's original casing reference.
        for(var base:List.of(flame_diesel,flame_gas,flame_napalm,flame_balefire))base.setCasing(new ItemStack(HbmContent.item("plate_steel"),2),500);
        BulletConfig[] source={flame_diesel,flame_gas,flame_napalm,flame_balefire,flame_diesel,flame_balefire,flame_diesel,flame_gas,flame_napalm,flame_balefire,flame_diesel,flame_gas,flame_napalm,flame_balefire};
        for(int i=0;i<ALL.size();i++)ALL.get(i).setCasing(source[i].casingItem,source[i].casingAmount);
    }
    private FlamerBullets(){}
}
