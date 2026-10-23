package com.hbm.bomb.conventional.grenade;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

/** The original shell/filling/fuze/extra ordinals, colors and executable trigger rules. */
public final class GrenadeParts {
    private GrenadeParts(){}
    public enum Shell {
        FRAG(4,30,.5,1),STICK(4,43,.25,1.5),TECH(2,30,.5,1),NUKE(1,43,.25,1.5);
        public final int stackLimit,drawDuration;public final double bounce,yeet;
        Shell(int stackLimit,int drawDuration,double bounce,double yeet){this.stackLimit=stackLimit;this.drawDuration=drawDuration;this.bounce=bounce;this.yeet=yeet;}
    }
    public enum Filling {
        POWDER(0x424242,0x939176,Shell.FRAG,Shell.STICK),HE(0x595533,0xA49D62,Shell.FRAG,Shell.STICK),DEMO(0x595533,0xDD4029,Shell.FRAG,Shell.STICK),INC(0x5A5A5A,0xFF5F21,Shell.FRAG,Shell.STICK),WP(0xDCDCDC,0xFF5F21,Shell.FRAG,Shell.STICK),CLUSTER(0x5A5A5A,0xFFC711,Shell.FRAG,Shell.STICK),EMP(0x93A1AC,0x00FFFF,Shell.TECH),PLASMA(0x655B2C,0x4CFF00,Shell.TECH),LASER(0x493A3A,0xFF0000,Shell.TECH),CLUSTER_HEAVY(0x5A5A5A,0xFF5F21,Shell.NUKE),NUCLEAR(0xDFD7A8,0xA49D62,Shell.NUKE),NUCLEAR_DEMO(0xDFD7A8,0xDD4029,Shell.NUKE),SCHRAB(0x00BDBD,0x000000,Shell.NUKE);
        public final int bodyColor,labelColor;public final EnumSet<Shell> compatibleShells;
        Filling(int bodyColor,int labelColor,Shell... shells){this.bodyColor=bodyColor;this.labelColor=labelColor;compatibleShells=EnumSet.noneOf(Shell.class);java.util.Collections.addAll(compatibleShells,shells);}
    }
    public enum Fuze {
        S3(0x000000),S7(0x404040),S15(0x808080),IMPACT(0xE36C17),AIRBURST(0x56A137);
        public final int bandColor;Fuze(int color){bandColor=color;}
        public void tick(UniversalGrenade grenade){int timer=grenade.timer();if(this==S3&&timer>=60||this==S7&&timer>=140||this==S15&&timer>=300)grenade.explode();else if(this==AIRBURST&&timer>=30){var start=grenade.position();var hit=grenade.level().clip(new ClipContext(start,start.add(0,-10,0),ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,grenade));if(hit.getType()==HitResult.Type.BLOCK)grenade.explode();}}
        public void impact(UniversalGrenade grenade,HitResult hit){if(this==IMPACT&&grenade.timer()>=10){grenade.setPos(hit.getLocation());grenade.explode();}}
    }
    public enum Extra {
        GLUE,PROXY_FUZE,FRAG_SLEEVE,TRIPLEX;
        public void tick(UniversalGrenade grenade){if(this==PROXY_FUZE&&grenade.timer()>=10&&grenade.timer()%3==0){for(var entity:grenade.level().getEntitiesOfClass(LivingEntity.class,new AABB(grenade.position(),grenade.position()).inflate(10))){if(entity==grenade.getThrower())continue;if(entity.distanceTo(grenade)<=10){grenade.explode();return;}}}}
        public void impact(UniversalGrenade grenade,HitResult hit){if(this==GLUE&&hit instanceof BlockHitResult block){grenade.setPos(hit.getLocation());var p=block.getBlockPos();grenade.getStuck(p.getX(),p.getY(),p.getZ(),block.getDirection().get3DDataValue());}}
        public void explode(UniversalGrenade grenade){
            if(this==FRAG_SLEEVE)GrenadeEffects.standardFragmentation(grenade,25);
            if(this==TRIPLEX){var item=UniversalGrenadeItem.make(grenade.shell(),grenade.filling(),Fuze.S3,null,1);Vec3 motion=rotateY(new Vec3(.25,0,0),grenade.level().random.nextDouble()*360);
                for(int i=0;i<3;i++){var triplet=new UniversalGrenade(grenade.level(),item).trail(1);triplet.setPos(grenade.position());triplet.setThrower(grenade.getThrower());triplet.setDeltaMovement(motion.x,.75,motion.z);grenade.level().addFreshEntity(triplet);motion=rotateY(motion,120);}}
        }
    }
    public static Vec3 rotateY(Vec3 v,double degrees){double r=degrees/180D*Math.PI,cos=Math.cos(r),sin=Math.sin(r);return new Vec3(v.x*cos+v.z*sin,v.y,v.z*cos-v.x*sin);}
    public static <T> T ordinal(T[] values,int ordinal){return values[Math.abs(ordinal%values.length)];}
}
