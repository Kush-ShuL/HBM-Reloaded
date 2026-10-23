package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Source ParticleMukeCloud/CloudBF, with the authored 5x5 atlas and deliberately constant vertical billboard height. */
public final class MukeCloud extends Particle {
    private final float friction;private final boolean balefire;
    public MukeCloud(ClientLevel level,double x,double y,double z,double vx,double vy,double vz,boolean balefire){super(level,x,y,z);this.balefire=balefire;xd=vx;yd=vy;zd=vz;setSize(.2F,.2F);setBoundingBox(getBoundingBox().move(0,-(double).1F,0));
        if(vy>0){friction=.9F;lifetime=vy>.1?92+random.nextInt(11)+(int)(vy*20):72+random.nextInt(11);}else if(vy==0){friction=.95F;lifetime=52+random.nextInt(11);}else{friction=.85F;lifetime=122+random.nextInt(31);age=80;}
    }
    @Override public void tick(){hasPhysics=age>2;xo=x;yo=y;zo=z;if(age++>=lifetime-2)remove();yd-=.04D*gravity;move(xd,yd,zd);xd*=friction;yd*=friction;zd*=friction;if(onGround){xd*=.7D;zd*=.7D;}}
    @Override public void move(double dx,double dy,double dz){var delta=hasPhysics?Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of()):new Vec3(dx,dy,dz);setBoundingBox(getBoundingBox().move(delta));setLocationFromBoundingbox();onGround=dy!=delta.y&&dy<0;if(dx!=delta.x)xd=0;if(dy!=delta.y)yd=0;if(dz!=delta.z)zd=0;}
    @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
    @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;var batch=MukeRenderTypes.begin(balefire?MukeRenderTypes.BALEFIRE_CLOUD:MukeRenderTypes.CLOUD);out=batch;if(age>lifetime)age=lifetime;int frame=age*25/lifetime;float size=1F/5F,u=frame%5*size,v=frame/5*size;var eye=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);MukeClient.billboard(out,camera,px,py,pz,3,1,1,1,1,u,u+size,v,v+size,true,true);}
    @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
