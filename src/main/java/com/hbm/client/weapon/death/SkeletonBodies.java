package com.hbm.client.weapon.death;

import java.util.*;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import static com.hbm.client.weapon.death.SkeletonParticle.Bone.*;

/** Original simple-class lookup and exact six-bone biped/zombie/villager definitions. */
public final class SkeletonBodies {
    public record BoneDefinition(SkeletonParticle.Bone type,float yaw,float pitch,double x,double y,double z){}
    public static final Map<String,Function<LivingEntity,List<BoneDefinition>>> SKULLANIZER=new HashMap<>();
    static{
        for(String name:List.of("RemotePlayer","LocalPlayer","EntityOtherPlayerMP","EntityClientPlayerMP","EntityPlayerSP"))SKULLANIZER.put(name,SkeletonBodies::biped);
        // Old zombie/skeleton metadata variants are distinct entity classes now.
        for(String name:List.of("Zombie","ZombieVillager","ZombifiedPiglin","Skeleton","WitherSkeleton","EntityZombie","EntitySkeleton","EntityPigZombie","EntityUndeadSoldier","ArmySoldier","PsychoSteve","SkeletonSoldier","ZombieFarmer","ZombieMiner","ZombiePigmanSoldier","ZombieSoldier"))SKULLANIZER.put(name,SkeletonBodies::zombie);
        for(String name:List.of("Villager","Witch","EntityVillager","EntityWitch"))SKULLANIZER.put(name,SkeletonBodies::villager);
        SKULLANIZER.put("EntityDummy",SkeletonBodies::biped);
    }
    private SkeletonBodies(){}
    private static Vec3 rotate(double x,double z,LivingEntity entity){double angle=-entity.yBodyRot*Math.PI/180,cos=Math.cos(angle),sin=Math.sin(angle);return new Vec3(x*cos+z*sin,0,z*cos-x*sin);}
    private static List<BoneDefinition> biped(LivingEntity entity){return body(entity,1.75,1.125,1.125,0,0,false);}
    private static List<BoneDefinition> zombie(LivingEntity entity){return body(entity,1.75,1.125,1.375,.25,-90,false);}
    private static List<BoneDefinition> villager(LivingEntity entity){return body(entity,1.6875,1,1.125,.25,-45,true);}
    private static List<BoneDefinition> body(LivingEntity entity,double skullY,double torsoY,double armY,double armForward,float armPitch,boolean villager){
        Vec3 arm=rotate(.375,0,entity),forward=rotate(0,armForward,entity),leg=rotate(.125,0,entity);double x=entity.getX(),y=entity.getY(),z=entity.getZ();float body=-entity.yBodyRot;
        return List.of(new BoneDefinition(villager?SKULL_VILLAGER:SKULL,-entity.yHeadRot,entity.getXRot(),x,y+skullY,z),new BoneDefinition(TORSO,body,0,x,y+torsoY,z),
                new BoneDefinition(LIMB,body,armPitch,x+arm.x+forward.x,y+armY,z+arm.z+forward.z),new BoneDefinition(LIMB,body,armPitch,x-arm.x+forward.x,y+armY,z-arm.z+forward.z),
                new BoneDefinition(LIMB,body,0,x+leg.x,y+.375,z+leg.z),new BoneDefinition(LIMB,body,0,x-leg.x,y+.375,z-leg.z));
    }
}
