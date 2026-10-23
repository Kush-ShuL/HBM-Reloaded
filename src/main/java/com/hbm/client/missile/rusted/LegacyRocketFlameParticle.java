package com.hbm.client.missile.rusted;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.mojang.blaze3d.vertex.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/** Original ParticleRocketFlame: ten seeded billboards, .91 drag, original orange-to-grey colour/expansion and life. */
final class LegacyRocketFlameParticle extends Particle {
    private static final AtomicInteger IDS=new AtomicInteger();private final int seed=IDS.incrementAndGet();
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/block/particle/particle_base.png");
    private static final ParticleRenderType TYPE=MukeRenderTypes.cloudTexture(TEXTURE,false);
    private final float particleScale;
    LegacyRocketFlameParticle(ClientLevel level,double x,double y,double z,double mx,double my,double mz,float scale,int life){super(level,x,y,z);random.nextFloat();random.nextFloat();lifetime=300+random.nextInt(50);particleScale=scale;lifetime=life;xd=mx;yd=my;zd=mz;}
    @Override public void tick(){xo=x;yo=y;zo=z;age++;if(age==lifetime)remove();xd*=.91;yd*=.91;zd*=.91;move(xd,yd,zd);}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    @Override public void render(VertexConsumer out,Camera camera,float partial){
        if(MukeRenderTypes.defer(this,partial))return;
        out=MukeRenderTypes.begin(TYPE);
        Random random=new Random(seed);var eye=camera.getPosition();float time=(float)age/lifetime;
        for(int i=0;i<10;i++){
            float add=random.nextFloat()*.3F,dark=1-Math.min(age/(lifetime*.25F),1);float r=dark+add,g=.6F*dark+add,b=add,alpha=(float)Math.pow(1-Math.min(time,1),.5)*.75F;
            float spread=((float)Math.pow(time*4F,1.5)+1)*particleScale,offset=.25F*particleScale,scale=(random.nextFloat()*.5F+.1F+time*2F)*particleScale;
            float px=(float)(xo+offset+(x-xo)*partial-eye.x+(random.nextGaussian()-1)*.2F*spread),py=(float)(yo+(y-yo)*partial-eye.y+(random.nextGaussian()-1)*.5F*spread),pz=(float)(zo+offset+(z-zo)*partial-eye.z+(random.nextGaussian()-1)*.2F*spread);
            var corners=new Vector3f[]{new Vector3f(-1,-1,0),new Vector3f(-1,1,0),new Vector3f(1,1,0),new Vector3f(1,-1,0)};float[] us={1,1,0,0},vs={1,0,0,1};
            for(int j=0;j<4;j++){var point=corners[j].rotate(camera.rotation()).mul(scale).add(px,py,pz);out.addVertex(point.x,point.y,point.z).setUv(us[j],vs[j]).setColor(Mth.clamp(r,0,1),Mth.clamp(g,0,1),Mth.clamp(b,0,1),alpha).setLight(240);}
        }
    }
}
