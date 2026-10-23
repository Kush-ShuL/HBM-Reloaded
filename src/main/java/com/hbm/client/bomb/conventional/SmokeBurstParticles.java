package com.hbm.client.bomb.conventional;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;

import com.hbm.bomb.conventional.SmokeBurstEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Original ParticleExSmoke: six id-stable billboards, 100..139 ticks, .7599999785423279 drag. */
public final class SmokeBurstParticles {
    private static final AtomicInteger IDS=new AtomicInteger();
    private static final ParticleRenderType TYPE=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/block/particle/particle_base.png"),false);
    private SmokeBurstParticles(){}
    public static void receive(SmokeBurstEffects packet){var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;var rand=level.random;for(int i=0;i<packet.count();i++){
        var particle=new Cloud(level,packet.x(),packet.y(),packet.z());particle.vy=rand.nextGaussian()*(1+packet.count()/(packet.radial()?50:100));particle.vx=rand.nextGaussian()*(1+packet.count()/(packet.radial()?50:150));particle.vz=rand.nextGaussian()*(1+packet.count()/(packet.radial()?50:150));if(!packet.radial()&&rand.nextBoolean())particle.vy=Math.abs(particle.vy);mc.particleEngine.add(particle);
    }}
    /** Source ClientProxy smoke/shock: its initial nextInt(360) is deliberately radians. */
    public static void shock(double x,double y,double z,int count,double strength){
        var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;count=Math.max(1,count);
        double vx=strength,vz=0;float angle=level.random.nextInt(360),cos=Mth.cos(angle),sin=Mth.sin(angle);double next=vx*cos+vz*sin;vz=vz*cos-vx*sin;vx=next;
        for(int i=0;i<count;i++){var cloud=new Cloud(level,x,y,z);cloud.vx=vx;cloud.vy=0;cloud.vz=vz;mc.particleEngine.add(cloud);angle=(float)Math.PI*2F/(float)count;cos=Mth.cos(angle);sin=Mth.sin(angle);next=vx*cos+vz*sin;vz=vz*cos-vx*sin;vx=next;}
    }
    /** Original ClientProxy smoke/shockRand; both angles deliberately retain source radian/int-division quirks. */
    public static void shockRand(double x,double y,double z,int count,double strength){
        var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;count=Math.max(1,count);
        double vx=strength,vz=0;float angle=level.random.nextInt(360),cos=Mth.cos(angle),sin=Mth.sin(angle);double next=vx*cos+vz*sin;vz=vz*cos-vx*sin;vx=next;
        for(int i=0;i<count;i++){double fraction=level.random.nextDouble();var cloud=new Cloud(level,x,y,z);cloud.vx=vx*fraction;cloud.vy=0;cloud.vz=vz*fraction;mc.particleEngine.add(cloud);angle=360/count;cos=Mth.cos(angle);sin=Mth.sin(angle);next=vx*cos+vz*sin;vz=vz*cos-vx*sin;vx=next;}
    }
    private static final class Cloud extends SingleQuadParticle {
        private final int id=IDS.getAndIncrement();double vx,vy,vz;
        Cloud(ClientLevel level,double x,double y,double z){super(level,x,y,z);lifetime=100+random.nextInt(40);hasPhysics=true;setBoundingBox(getBoundingBox().move(0,-.1,0));}
        @Override public void tick(){xo=x;yo=y;zo=z;alpha=1-(float)age/lifetime;if(++age==lifetime)remove();vx*=.7599999785423279D;vy*=.7599999785423279D;vz*=.7599999785423279D;move(vx,vy,vz);}
        @Override public void move(double dx,double dy,double dz){var delta=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(delta));setLocationFromBoundingbox();onGround=dy!=delta.y&&dy<0;if(dx!=delta.x)vx=0;if(dy!=delta.y)vy=0;if(dz!=delta.z)vz=0;}
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
        @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;out=MukeRenderTypes.begin(TYPE);var random=new Random(id);var cam=camera.getPosition();for(int i=0;i<6;i++){rCol=gCol=bCol=random.nextFloat()*.25F+.25F;quadSize=random.nextFloat()+.5F;float px=(float)(Mth.lerp(partial,xo,x)-cam.x+(random.nextGaussian()-1D)*.75F),py=(float)(Mth.lerp(partial,yo,y)-cam.y+(random.nextGaussian()-1D)*.75F),pz=(float)(Mth.lerp(partial,zo,z)-cam.z+(random.nextGaussian()-1D)*.75F);renderRotatedQuad(out,camera.rotation(),px,py,pz,partial);}}
        @Override protected float getU0(){return 0;}@Override protected float getU1(){return 1;}@Override protected float getV0(){return 0;}@Override protected float getV1(){return 1;}
        @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(float partial){return net.minecraft.world.phys.AABB.INFINITE;}
        @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    }
}
