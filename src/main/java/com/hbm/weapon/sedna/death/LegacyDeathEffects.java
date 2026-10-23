package com.hbm.weapon.sedna.death;

import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.explosion.ExplosionDeathEffects;
import com.hbm.registry.HbmSounds;
import java.util.Set;
import net.minecraft.sounds.*;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.neoforged.bus.api.IEventBus;

/** X5846 ConfettiUtil's independent damage-class checks and exact packet/sound parameters. */
public final class LegacyDeathEffects {
    private static boolean registered;
    private static final Set<String> METAL_MOBS=Set.of("EntityCyberCrab","EntityTeslaCrab","EntityTaintCrab","EntityFBIDrone","EntityRADBeast","EntityUFO","EntityBOTPrimeBase");
    private LegacyDeathEffects(){}
    public static void register(IEventBus bus){if(registered)return;registered=true;bus.addListener(DeathParticlePacket::register);ExplosionDeathEffects.register(LegacyDeathEffects::decideConfetti);}
    public static void decideConfetti(LivingEntity entity,DamageSource source){
        if(entity.isAlive())return;
        if(DamageClass.LASER.isApplicable(source.getMsgId()))pulverize(entity);
        if(DamageClass.ELECTRIC.isApplicable(source.getMsgId()))pulverize(entity);
        if(DamageClass.PLASMA.isApplicable(source.getMsgId()))cremate(entity);
        if(source.is(DamageTypeTags.IS_EXPLOSION))gib(entity);
        if(source.is(DamageTypeTags.IS_FIRE))cremate(entity);
    }
    private static int ashesCount(LivingEntity entity){return Mth.clamp((int)(entity.getBbWidth()*entity.getBbHeight()*entity.getBbWidth()*25),5,50);}
    public static void pulverize(LivingEntity entity){DeathParticlePacket.ashes(entity,ashesCount(entity),.125F);DeathParticlePacket.skeleton(entity,1);disintegrationSound(entity);}
    public static void cremate(LivingEntity entity){DeathParticlePacket.ashes(entity,ashesCount(entity),.125F);DeathParticlePacket.skeleton(entity,.25F);disintegrationSound(entity);}
    private static void disintegrationSound(LivingEntity entity){entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("weapon.fire.disintegration"),SoundSource.NEUTRAL,2,.9F+entity.getRandom().nextFloat()*.2F);}
    public static void gib(LivingEntity entity){
        if(entity instanceof Ocelot)return;
        int type=0;if(entity instanceof Slime||entity instanceof Creeper)type=1;if(entity instanceof AbstractGolem||entity instanceof Blaze)type=2;
        for(Class<?> owner=entity.getClass();owner!=null;owner=owner.getSuperclass())if(METAL_MOBS.contains(owner.getSimpleName()))type=2;
        DeathParticlePacket.skeletonGib(entity,.25F);
        if(entity instanceof AbstractSkeleton)return;
        DeathParticlePacket.giblets(entity,type,0);entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,SoundSource.NEUTRAL,2,.95F+entity.getRandom().nextFloat()*.2F);
    }
}
