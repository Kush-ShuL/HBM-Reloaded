package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Exact ParticleRocketFlame: ten stable subpuffs,300..349life,source spread/fade/colors/full block light. */
public final class LegacyRocketFlameParticle extends Particle {
    // Modern particles are not Entities. This stable particle id replaces only the old shape-seeding entity id.
    private static final AtomicInteger NEXT_ID=new AtomicInteger();
    private final Puff[] puffs=new Puff[10];
    private float particleScale=1;
    public LegacyRocketFlameParticle(ClientLevel level,double x,double y,double z){
        super(level,x,y,z);lifetime=300+random.nextInt(50);setSize(.2F,.2F);xd=yd=zd=0;
        var shape=new Random(NEXT_ID.incrementAndGet());
        for(int i=0;i<puffs.length;i++)puffs[i]=new Puff(shape.nextFloat()*.3F,shape.nextFloat()*.5F+.1F,shape.nextGaussian()-1D,shape.nextGaussian()-1D,shape.nextGaussian()-1D);
    }
    public LegacyRocketFlameParticle setScale(float scale){particleScale=scale;return this;}
    public LegacyRocketFlameParticle setMaxAge(int maxAge){lifetime=maxAge;return this;}
    @Override public void tick(){xo=x;yo=y;zo=z;age++;if(age==lifetime)remove();xd*=.91D;yd*=.91D;zd*=.91D;move(xd,yd,zd);}
    @Override public void render(VertexConsumer unused,Camera camera,float partial){
        if(MukeRenderTypes.defer(this,partial))return;
        var vertices=MukeRenderTypes.begin(MukeRenderTypes.ROCKET_FLAME);var eye=camera.getPosition();
        float fraction=(float)age/lifetime;
        float dark=1-Math.min((float)age/(lifetime*.25F),1);
        float alpha=(float)Math.pow(1-Math.min(fraction,1),.5)*.75F;
        float spread=((float)Math.pow(fraction*4F,1.5)+1F)*particleScale;
        float sideOffset=(float)(.25*particleScale);
        for(var puff:puffs){
            float size=(puff.size+fraction*2F)*particleScale;
            float px=(float)((xo+sideOffset+(x-xo)*partial-eye.x)+puff.x*.2F*spread);
            float py=(float)((yo+(y-yo)*partial-eye.y)+puff.y*.5F*spread);
            float pz=(float)((zo+sideOffset+(z-zo)*partial-eye.z)+puff.z*.2F*spread);
            // Legacy Tessellator clamps channels before byte packing. Modern float vertex colors do not.
            MukeClient.billboard(vertices,camera,px,py,pz,size,Mth.clamp(dark+puff.add,0,1),Mth.clamp(.6F*dark+puff.add,0,1),puff.add,alpha,0,1,0,1,false,true);
        }
    }
    @Override public AABB getRenderBoundingBox(float partial){
        double px=Mth.lerp(partial,xo,x),py=Mth.lerp(partial,yo,y),pz=Mth.lerp(partial,zo,z);
        float fraction=(float)age/lifetime,spread=((float)Math.pow(fraction*4F,1.5)+1F)*particleScale,side=(float)(.25*particleScale);
        double minX=Double.POSITIVE_INFINITY,minY=minX,minZ=minX,maxX=Double.NEGATIVE_INFINITY,maxY=maxX,maxZ=maxX;
        for(var puff:puffs){double size=Math.abs((puff.size+fraction*2F)*particleScale)*Math.sqrt(2),cx=px+side+puff.x*.2F*spread,cy=py+puff.y*.5F*spread,cz=pz+side+puff.z*.2F*spread;minX=Math.min(minX,cx-size);minY=Math.min(minY,cy-size);minZ=Math.min(minZ,cz-size);maxX=Math.max(maxX,cx+size);maxY=Math.max(maxY,cy+size);maxZ=Math.max(maxZ,cz+size);}
        return new AABB(minX,minY,minZ,maxX,maxY,maxZ);
    }
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    private record Puff(float add,float size,double x,double y,double z){}
}
