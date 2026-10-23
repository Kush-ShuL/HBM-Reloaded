package com.hbm.weapon.sedna.gun.grenade40;

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

/** The five actual XFactory40mm impact branches; persistent fire reuses its real grenade module. */
public final class Grenade40Impacts {
    private Grenade40Impacts() {}
    private static boolean earlySelf(EntityBulletBaseMK4 bullet, HitResult impact) {
        return impact instanceof EntityHitResult hit && bullet.tickCount < 3 && hit.getEntity() == bullet.getThrower();
    }
    public static void standard(EntityBulletBaseMK4 bullet, HitResult impact) {
        Lego.standardExplode(bullet, impact, 5F);
        bullet.setDead();
    }
    public static void heat(EntityBulletBaseMK4 bullet, HitResult impact) {
        if (earlySelf(bullet, impact)) return;
        Lego.standardExplode(bullet, impact, 3.5F);
        bullet.setDead();
        if (impact instanceof EntityHitResult hit) {
            var source = BulletConfig.getDamage(bullet, bullet.getThrower(), DamageClass.EXPLOSIVE);
            if (hit.getEntity() instanceof LivingEntity living)
                LegacyEntityDamage.attackEntityFromNT(living, source, bullet.damage * 3F, true, true, .5F, 3F, .15F);
            else hit.getEntity().hurt(source, bullet.damage * 3F);
        }
    }
    public static void demolition(EntityBulletBaseMK4 bullet, HitResult impact) {
        if (earlySelf(bullet, impact)) return;
        var hit = impact.getLocation();
        var explosion = new ExplosionVNT(bullet.level(), hit.x, hit.y, hit.z, 5F, bullet.getThrower());
        explosion.setBlockAllocator(new BlockAllocatorStandard());
        explosion.setBlockProcessor(new BlockProcessorStandard());
        explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.damage));
        explosion.setPlayerProcessor(new PlayerProcessorStandard());
        explosion.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        explosion.explode();
        bullet.setDead();
    }
    public static void incendiary(EntityBulletBaseMK4 bullet, HitResult impact) { fire(bullet, impact, false, 200); }
    public static void phosphorus(EntityBulletBaseMK4 bullet, HitResult impact) { fire(bullet, impact, true, 400); }
    private static void fire(EntityBulletBaseMK4 bullet, HitResult impact, boolean phosphorus, int duration) {
        if (earlySelf(bullet, impact)) return;
        var world = bullet.level();
        var hit = impact.getLocation();
        Lego.standardExplode(bullet, impact, 3F);
        var fire = new LingeringFire(world).area(5, 2).duration(duration).type(phosphorus ? 2 : 0);
        fire.setPos(hit);
        world.addFreshEntity(fire);
        bullet.setDead();
        var center = BlockPos.containing(hit);
        for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
            var position = center.offset(dx, dy, dz);
            if (world.isEmptyBlock(position)) for (Direction side : Direction.values()) {
                var neighbor = position.relative(side);
                if (world.getBlockState(neighbor).isFlammable(world, neighbor, side.getOpposite())) {
                    world.setBlockAndUpdate(position, Blocks.FIRE.defaultBlockState());
                    break;
                }
            }
        }
    }
}
