package com.hbm.weapon.sedna.gun.quadro;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Original XFactoryRocket.LAMBDA_STEERING_ACCELERATE and steeringAccelerate. */
public final class QuadroGuidance {
    private QuadroGuidance(){}
    public static void update(Entity entity){
        var bullet=(EntityBulletBaseMK4)entity;
        if(!(bullet.getThrower() instanceof Player player)){if(bullet.accel<7)bullet.accel+=.4D;return;}
        var held=player.getMainHandItem();boolean noSteer=held.isEmpty()||!(held.getItem() instanceof ItemGunBaseNT)||!ItemGunBaseNT.getIsAiming(held);
        steeringAccelerate(entity,noSteer);
    }
    /** Exact common XFactoryRocket.steeringAccelerate; NCR deliberately passesfalse even without an aiming gun. */
    public static void steeringAccelerate(Entity entity,boolean noSteer){
        var bullet=(EntityBulletBaseMK4)entity;
        if(bullet.accel<4)bullet.accel+=.4D;
        if(!(bullet.getThrower() instanceof Player player))return;
        if(LegacyVector.createVectorHelper(bullet.getX()-player.getX(),bullet.getY()-player.getY(),bullet.getZ()-player.getZ()).lengthVector()>100)return;
        if(noSteer)return;
        Vec3 hit=QuadroTargetRay.trace(player,200D);if(hit==null)return;
        var delta=LegacyVector.createVectorHelper(hit.x-bullet.getX(),hit.y-bullet.getY(),hit.z-bullet.getZ());
        if(delta.lengthVector()<3)return;
        // This source branch uses Vec3.normalize (component division), unlike MK4's Vec3NT.normalizeSelf.
        var normalized=delta.normalize();var motion=bullet.getDeltaMovement();
        double speed=LegacyVector.createVectorHelper(motion.x,motion.y,motion.z).lengthVector();
        bullet.setDeltaMovement(normalized.xCoord*speed,normalized.yCoord*speed,normalized.zCoord*speed);
    }
}
