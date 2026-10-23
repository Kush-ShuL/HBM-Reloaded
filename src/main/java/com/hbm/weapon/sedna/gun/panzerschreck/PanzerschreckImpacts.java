package com.hbm.weapon.sedna.gun.panzerschreck;

import com.hbm.bomb.conventional.grenade.LingeringFire;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.factory.Lego;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Exact XFactoryRocket callbacks: these intentionally differ from 40mm ammunition. */
public final class PanzerschreckImpacts {
    private PanzerschreckImpacts() {}
    private static boolean earlyEntity(EntityBulletBaseMK4 bullet,HitResult impact){return impact instanceof EntityHitResult && bullet.tickCount<3;}
    public static void standard(EntityBulletBaseMK4 bullet,HitResult impact){
        if(earlyEntity(bullet,impact))return;
        Lego.standardExplode(bullet,impact,5F);bullet.setDead();
    }
    public static void heat(EntityBulletBaseMK4 bullet,HitResult impact){
        if(earlyEntity(bullet,impact))return;
        Lego.standardExplode(bullet,impact,3.5F);bullet.setDead();
        if(impact instanceof EntityHitResult hit){
            var damage=BulletConfig.getDamage(bullet,bullet.getThrower(),DamageClass.EXPLOSIVE);
            if(hit.getEntity() instanceof LivingEntity living)
                LegacyEntityDamage.attackEntityFromNT(living,damage,bullet.damage*3F,true,true,.5F,5F,.2F);
            else hit.getEntity().hurt(damage,bullet.damage*3F);
        }
    }
    public static void demolition(EntityBulletBaseMK4 bullet,HitResult impact){
        if(earlyEntity(bullet,impact))return;
        var hit=impact.getLocation();
        var explosion=new ExplosionVNT(bullet.level(),hit.x,hit.y,hit.z,5F,bullet.getThrower());
        explosion.setBlockAllocator(new BlockAllocatorStandard());
        explosion.setBlockProcessor(new BlockProcessorStandard());
        explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1,bullet.damage));
        explosion.setPlayerProcessor(new PlayerProcessorStandard());
        explosion.setSFX(new ExplosionEffectWeapon(10,2.5F,1F));explosion.explode();bullet.setDead();
    }
    public static void incendiary(EntityBulletBaseMK4 bullet,HitResult impact){fire(bullet,impact,false,300);}
    public static void phosphorus(EntityBulletBaseMK4 bullet,HitResult impact){fire(bullet,impact,true,600);}
    private static void fire(EntityBulletBaseMK4 bullet,HitResult impact,boolean phosphorus,int duration){
        if(earlyEntity(bullet,impact))return;
        var world=bullet.level();var hit=impact.getLocation();
        Lego.standardExplode(bullet,impact,3F);
        var fire=new LingeringFire(world).area(6,2).duration(duration).type(phosphorus?2:0);
        fire.setPos(hit);world.addFreshEntity(fire);bullet.setDead();
        var center=BlockPos.containing(hit);
        for(int dx=-2;dx<=2;dx++)for(int dy=-2;dy<=2;dy++)for(int dz=-2;dz<=2;dz++){
            var pos=center.offset(dx,dy,dz);
            if(world.isEmptyBlock(pos))for(Direction side:Direction.values()){
                var neighbor=pos.relative(side);
                if(world.getBlockState(neighbor).isFlammable(world,neighbor,side.getOpposite())){
                    world.setBlockAndUpdate(pos,Blocks.FIRE.defaultBlockState());break;
                }
            }
        }
    }
}
