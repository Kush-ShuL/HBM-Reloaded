package com.hbm.explosion;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/** Original spatial relocation and ignition branches of X5846 ExplosionChaos. */
public final class ExplosionChaos {
    private static final Random random=new Random();
    private ExplosionChaos() { }
    /** Actual source hardenVirus; only the crystal-family continuation prepares cold mutation neighbours. */
    public static void hardenVirus(Level world,int x,int y,int z,int strength){com.hbm.block.sourcecrystal.CrystalActions.hardenVirus(world,x,y,z,strength);}
    public static void cluster(Level world,double x,double y,double z,int count,float yaw,float pitch,float yawRand,float pitchRand,float speed) {
        for(int i=0;i<count;i++) {
            var bullet=new com.hbm.entity.projectile.EntityBulletBaseMK4(world,
                    com.hbm.weapon.sedna.CatapultBulletConfigs.CLUSTER_SUBMUNITION,50F,0F,
                    yaw+(float)(yawRand*world.random.nextGaussian()),
                    pitch+(float)(pitchRand*world.random.nextGaussian()));
            bullet.setPos(x,y,z);
            bullet.setDeltaMovement(bullet.getDeltaMovement().scale(speed));
            world.addFreshEntity(bullet);
        }
    }
    public static void floater(Level world,int x,int y,int z,int radius,int height) {
        int r22=radius*radius/2;
        for(int xx=-radius;xx<radius;xx++) for(int yy=-radius;yy<radius;yy++) for(int zz=-radius;zz<radius;zz++) if(xx*xx+yy*yy+zz*zz<r22) {
            BlockPos pos=new BlockPos(x+xx,y+yy,z+zz);var saved=world.getBlockState(pos);
            world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
            if(!saved.isAir()) {
                BlockPos destination=pos.above(height);
                // Source deliberately recreates the block before restoring metadata; TE NBT is not moved.
                world.setBlockAndUpdate(destination,saved.getBlock().defaultBlockState());
                world.setBlock(destination,saved,Block.UPDATE_CLIENTS);
            }
        }
    }
    public static void move(Level world,int x,int y,int z,int radius,int a,int b,int c) {
        int diameter=(int)(radius*2F);
        var box=new AABB(Mth.floor(x-(double)radius-1),Mth.floor(y-(double)radius-1),Mth.floor(z-(double)radius-1),Mth.floor(x+(double)radius+1),Mth.floor(y+(double)radius+1),Mth.floor(z+(double)radius+1));
        for(Entity entity:world.getEntities((Entity)null,box)) if((float)Math.sqrt(entity.distanceToSqr(x,y,z))/diameter<=1D) {
            double dx=entity.getX()-x,dy=entity.getY()+entity.getEyeHeight()-y,dz=entity.getZ()-z;
            if(entity instanceof Mob&&!(entity instanceof Sheep)) entity.setCustomName(Component.literal(random.nextInt(2)==0?"Dinnerbone":"Grumm"));
            if(entity instanceof Sheep) entity.setCustomName(Component.literal("jeb_"));
            if((float)Math.sqrt(dx*dx+dy*dy+dz*dz)<radius) entity.setPos(entity.getX()+a,entity.getY()+b,entity.getZ()+c);
        }
    }
    /** Actual source lung-or-blistering mask protection, one filter wear, otherwise the original five symptoms. */
    public static void poison(Level world,double x,double y,double z,double range) {
        for(var entity:world.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,new AABB(x-range,y-range,z-range,x+range,y+range,z+range))) {
            if(entity.distanceToSqr(x,y,z)>range*range)continue;
            if(com.hbm.exposure.HbmExposure.protectAndUseFilter(entity,com.hbm.exposure.ExposureProtection.Hazard.GAS_LUNG,1)||com.hbm.exposure.HbmExposure.protectAndUseFilter(entity,com.hbm.exposure.ExposureProtection.Hazard.GAS_BLISTERING,1))continue;
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.BLINDNESS,100,0));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON,400,2));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER,20,1));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,600,1));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN,600,2));
        }
    }
    /** Source ExplosionChaos.c: wear all four pieces before hazmat/mutation protection and taint conversion. */
    public static void c(Level world,double x,double y,double z,double range) {
        for(var entity:world.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,new AABB(x-range,y-range,z-range,x+range,y+range,z+range))) {
            if((float)Math.sqrt(entity.distanceToSqr(x,y,z))>range)continue;
            for(int slot=0;slot<4;slot++)com.hbm.bomb.conventional.MistExposure.damageSuit(entity,slot,25);
            if(com.hbm.bomb.conventional.MistExposure.checkForHazmat(entity))continue;
            if(entity.hasEffect(com.hbm.item.medicine.MedicineEffects.TAINT)) {
                entity.removeEffect(com.hbm.item.medicine.MedicineEffects.TAINT);
                entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(com.hbm.exposure.ExposureEffects.MUTATION,72000,0,false,true));
            }
            entity.hurt(com.hbm.damage.HbmDamageSources.source(world,com.hbm.damage.HbmDamageSources.cloud),5);
        }
    }
    public static void igniteFlammableBlocks(Level world,int x,int y,int z,int bound) {
        int r22=bound*bound/2;
        for(int xx=-bound;xx<bound;xx++) for(int yy=-bound;yy<bound;yy++) for(int zz=-bound;zz<bound;zz++) {
            int squareX=xx*xx,squareXY=squareX+yy*yy,squareXYZ=squareXY+zz*zz;
            if(squareXYZ<r22) {
                BlockPos pos=new BlockPos(x+xx,y+yy,z+zz);
                // Preserve the original unusual squared-coordinate context passed to isFlammable.
                if(world.getBlockState(pos).getFlammability(world,new BlockPos(squareX,squareXY,squareXYZ),Direction.UP)>0&&world.getBlockState(pos.above()).isAir())
                    world.setBlockAndUpdate(pos.above(),Blocks.FIRE.defaultBlockState());
            }
        }
    }
    public static void igniteAllBlocks(Level world,int x,int y,int z,int bound) {
        int r22=bound*bound/2;
        for(int xx=-bound;xx<bound;xx++) for(int yy=-bound;yy<bound;yy++) for(int zz=-bound;zz<bound;zz++) if(xx*xx+yy*yy+zz*zz<r22) {
            BlockPos pos=new BlockPos(x+xx,y+yy,z+zz);var above=world.getBlockState(pos.above());
            if((above.isAir()||above.is(Blocks.SNOW))&&!world.getBlockState(pos).isAir()) world.setBlockAndUpdate(pos.above(),Blocks.FIRE.defaultBlockState());
        }
    }
}
