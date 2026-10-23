package com.hbm.weapon.sedna.gun.kinetic;

import com.hbm.block.falling.LegacyFallingRules;
import com.hbm.block.crt.DecoCRTBlock;
import com.hbm.bomb.nuclear.NuclearPlasmaBlastEffects;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.explosion.ExplosionDeathEffects;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.ShotSensitiveBlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** XFactory12ga's actual Shredder beam, plasma ricochets and pulse effects. */
public final class KineticImpacts {
    private KineticImpacts() {}

    public static BulletConfig submunition(String name, BulletConfig source) {
        BulletConfig result = copy("hbm:12ga/shredder_sub/" + name, source);
        return result.setRicochetAngle(90).setRicochetCount(3).setVel(.5F).setLife(50)
                .setupDamageClass(DamageClass.PLASMA).setOnRicochet(KineticImpacts::ricochet);
    }

    public static BulletConfig beam(String name, BulletConfig original, BulletConfig submunition) {
        return new BulletConfig("hbm:12ga/shredder_beam/" + name).setBeam().setRenderRotations(false).setLife(5)
                .setDamage(original.damageMult * original.projectilesMax).setupDamageClass(DamageClass.LASER)
                .setItem(original.ammo).setCasing(original.casing)
                .setOnBeamImpact((beam, impact) -> hit(beam, impact, submunition));
    }

    private static void hit(EntityBulletBeamBase beam, HitResult impact, BulletConfig submunition) {
        Level level = beam.level();
        int projectiles = submunition.projectilesMin;
        if (submunition.projectilesMax > submunition.projectilesMin)
            projectiles += level.random.nextInt(submunition.projectilesMax - submunition.projectilesMin + 1);
        if (impact instanceof BlockHitResult block) {
            Vec3 normal = Vec3.atLowerCornerOf(block.getDirection().getNormal());
            Vec3 location = block.getLocation().add(normal.scale(.1));
            var shifted = new BlockHitResult(location, block.getDirection(), block.getBlockPos(), block.isInside());
            pulse(level, shifted, beam.getYRot(), beam.getXRot());
            DamageSource damage = BulletConfig.getDamage(beam, beam.getThrower(), DamageClass.LASER);
            for (Entity entity : level.getEntities(beam, new AABB(location, location).inflate(.75)))
                damage(entity, damage, beam.damage);
            for (int i = 0; i < projectiles; i++) {
                var bullet = new EntityBulletBaseMK4(level, beam.getThrower(), submunition,
                        beam.damage * submunition.damageMult, .2F, location.x, location.y, location.z,
                        normal.x, normal.y, normal.z);
                level.addFreshEntity(bullet);
            }
        }
        if (impact instanceof EntityHitResult hit) {
            pulse(level, impact, beam.getYRot(), beam.getXRot());
            Vec3 location = hit.getLocation();
            for (int i = 0; i < projectiles; i++) {
                Vec3 direction = new Vec3(level.random.nextGaussian(), level.random.nextGaussian(), level.random.nextGaussian()).normalize();
                var bullet = new EntityBulletBaseMK4(level, beam.getThrower(), submunition,
                        beam.damage * submunition.damageMult, .2F, location.x, location.y, location.z,
                        direction.x, direction.y, direction.z);
                level.addFreshEntity(bullet);
            }
        }
    }

    private static void damage(Entity entity, DamageSource source, float damage) {
        if (!entity.isAlive()) return;
        if (entity instanceof LivingEntity living) {
            LegacyEntityDamage.attackEntityFromNT(living, source, damage, true, false, 0D, 0F, 0F);
            if (!living.isAlive()) ExplosionDeathEffects.decide(living, source);
        } else entity.hurt(source, damage);
    }

    private static void ricochet(EntityBulletBaseMK4 bullet, HitResult impact) {
        if (!(impact instanceof BlockHitResult hit)) return;
        Level level = bullet.level();
        var position = hit.getBlockPos();
        var state = level.getBlockState(position);
        if (LegacyFallingRules.material(state).name().equals("glass")) {
            level.destroyBlock(position, false);
            bullet.setPos(hit.getLocation());
            return;
        }
        if (state.getBlock() instanceof ShotSensitiveBlock sensitive) sensitive.onShot(level, position);
        if (state.getBlock() instanceof DecoCRTBlock crt) crt.breakScreen(level, position);
        Vec3 face = Vec3.atLowerCornerOf(hit.getDirection().getNormal());
        Vec3 velocity = bullet.getDeltaMovement().normalize();
        double angle = Math.abs(Math.acos(Math.max(-1, Math.min(1, velocity.dot(face)))) * 180 / Math.PI - 90);
        if (angle <= bullet.config.ricochetAngle) {
            pulse(level, hit, bullet.getYRot(), bullet.getXRot());
            DamageSource source = BulletConfig.getDamage(bullet, bullet.getThrower(), DamageClass.PLASMA);
            for (Entity entity : level.getEntities(bullet, new AABB(bullet.position(), bullet.position()).inflate(.5)))
                damage(entity, source, bullet.damage);
            bullet.ricochets++;
            if (bullet.ricochets > bullet.config.maxRicochetCount) {
                bullet.setPos(hit.getLocation());
                bullet.setDead();
            }
            var motion = bullet.getDeltaMovement();
            bullet.setDeltaMovement(switch (hit.getDirection().getAxis()) {
                case X -> motion.multiply(-1, 1, 1);
                case Y -> motion.multiply(1, -1, 1);
                case Z -> motion.multiply(1, 1, -1);
            });
            bullet.setPos(hit.getLocation());
            bullet.forceNetworkTeleport();
        } else {
            bullet.setPos(hit.getLocation());
            bullet.setDead();
        }
    }

    private static void pulse(Level level, HitResult hit, float yaw, float pitch) {
        Vec3 location = hit.getLocation();
        if (hit instanceof BlockHitResult block) {
            switch (block.getDirection()) {
                case UP, DOWN -> { yaw = 0; pitch = 0; }
                case NORTH -> { yaw = 0; pitch = 90; }
                case SOUTH -> { yaw = 180; pitch = 90; }
                case EAST -> { yaw = 90; pitch = 90; }
                case WEST -> { yaw = 270; pitch = 90; }
            }
            location = location.add(Vec3.atLowerCornerOf(block.getDirection().getNormal()).scale(.05));
        }
        NuclearPlasmaBlastEffects.send(level, location.x, location.y, location.z, .5F, .5F, 1, .75F, pitch, yaw, 100);
    }

    /** The original clone is a shallow configuration copy; registry identity is newly stable. */
    private static BulletConfig copy(String name, BulletConfig source) {
        var result = new BulletConfig(name);
        result.ammo = source.ammo; result.casingItem = source.casingItem; result.casingAmount = source.casingAmount;
        result.ammoReloadCount = source.ammoReloadCount; result.casing = source.casing; result.renderer = source.renderer;
        result.pType = source.pType; result.onImpactBeam = source.onImpactBeam; result.rendererBeam = source.rendererBeam;
        result.velocity = source.velocity; result.spread = source.spread; result.wear = source.wear; result.damageMult = source.damageMult;
        result.armorThresholdNegation = source.armorThresholdNegation; result.armorPiercingPercent = source.armorPiercingPercent;
        result.knockbackMult = source.knockbackMult; result.headshotMult = source.headshotMult; result.ricochetAngle = source.ricochetAngle;
        result.projectilesMin = source.projectilesMin; result.projectilesMax = source.projectilesMax;
        result.maxRicochetCount = source.maxRicochetCount; result.expires = source.expires; result.selfDamageDelay = source.selfDamageDelay;
        result.damageFalloffByPen = source.damageFalloffByPen; result.impactsEntities = source.impactsEntities;
        result.doesPenetrate = source.doesPenetrate; result.isSpectral = source.isSpectral; result.blackPowder = source.blackPowder;
        result.renderRotations = source.renderRotations; result.clientKeepsRotation = source.clientKeepsRotation;
        result.gravity = source.gravity; result.dmgClass = source.dmgClass; result.onUpdate = source.onUpdate;
        result.onImpact = source.onImpact; result.onRicochet = source.onRicochet; result.onEntityHit = source.onEntityHit;
        return result;
    }
}
