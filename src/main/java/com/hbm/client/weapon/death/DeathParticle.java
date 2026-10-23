package com.hbm.client.weapon.death;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Original EntityFX 0.2 box is centered vertically on its logical position. */
abstract class DeathParticle extends Particle {
    private static final AtomicInteger IDS=new AtomicInteger();
    protected final int legacyId=IDS.getAndIncrement();
    protected float particleScale;
    protected DeathParticle(ClientLevel level,double x,double y,double z){super(level,x,y,z);particleScale=(random.nextFloat()*.5F+.5F)*2;setBoundingBox(getBoundingBox().move(0,-.1,0));}
    @Override public void move(double dx,double dy,double dz){
        var actual=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());
        setBoundingBox(getBoundingBox().move(actual));setLocationFromBoundingbox();onGround=dy!=actual.y&&dy<0;
        if(dx!=actual.x)xd=0;if(dy!=actual.y)yd=0;if(dz!=actual.z)zd=0;
    }
    @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
}
