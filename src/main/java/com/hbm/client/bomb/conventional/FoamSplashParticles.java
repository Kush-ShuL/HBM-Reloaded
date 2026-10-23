package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.FoamSplashEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/** Source ParticleFoam, including burst/peak/settle phases and fifteen-point bubble trails. */
public final class FoamSplashParticles {
    private static final AtomicInteger IDS=new AtomicInteger();
    private static final ParticleRenderType TYPE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/block/particle/particle_base.png"));RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.depthMask(true);RenderSystem.disableCull();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
    private FoamSplashParticles(){}
    public static void receive(FoamSplashEffects packet){var mc=Minecraft.getInstance();if(mc.level==null)return;for(int i=0;i<packet.count();i++){mc.level.random.nextFloat();mc.particleEngine.add(new Foam(mc.level,packet.x(),packet.y(),packet.z()));}}
    private static final class Foam extends SingleQuadParticle {
        private final int id=IDS.getAndIncrement();private final List<Vec3> trail=new ArrayList<>();private int phase;
        Foam(ClientLevel level,double x,double y,double z){super(level,x,y,z);lifetime=60+random.nextInt(60);gravity=.005F+random.nextFloat()*.015F;yd=2+random.nextFloat()*3;double angle=random.nextDouble()*Math.PI*2,strength=random.nextDouble()*.5;xd=Math.cos(angle)*strength;zd=Math.sin(angle)*strength;quadSize=.3F+random.nextFloat()*.7F;setSize(.2F,.2F);setBoundingBox(getBoundingBox().move(0,-(double).1F,0));hasPhysics=true;lifetime=50;xd=yd=zd=0;}
        @Override public void tick(){xo=x;yo=y;zo=z;trail.addFirst(new Vec3(x,y,z));while(trail.size()>15)trail.removeLast();if(++age==lifetime)remove();float ratio=(float)age/lifetime;
            if(ratio<.3F){phase=0;if(ratio<.15F)yd+=.05F*6;else yd+=.05F*(1-ratio/.3F)*2;quadSize=1+.5F*(ratio/.3F);}else if(ratio<.6F){phase=1;yd*=.98F;quadSize=1.5F;}else{phase=2;yd-=gravity;quadSize=1.5F*(1-(ratio-.6F)/.4F*.7F);}
            alpha=.8F*(1-ratio*ratio);xd+=(random.nextFloat()-.5F)*.15F;zd+=(random.nextFloat()-.5F)*.15F;xd*=.96F;yd*=.96F;zd*=.96F;move(xd,yd,zd);if(onGround||level.getBlockStates(getBoundingBox()).anyMatch(s->s.is(Blocks.COBWEB)))remove();
        }
        @Override public void move(double dx,double dy,double dz){var delta=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(delta));setLocationFromBoundingbox();onGround=dy!=delta.y&&dy<0;if(dx!=delta.x)xd=0;if(dy!=delta.y)yd=0;if(dz!=delta.z)zd=0;}
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
        @Override public void render(VertexConsumer out,Camera camera,float partial){float scale=quadSize,opacity=alpha;bubbles(out,camera,partial,new Vec3(x,y,z),scale,opacity);for(int i=1;i<trail.size();i++)bubbles(out,camera,partial,trail.get(i),scale*(1-(float)i/15),opacity*(1-(float)i/15)*.7F);quadSize=scale;alpha=opacity;}
        private void bubbles(VertexConsumer out,Camera camera,float partial,Vec3 position,float scale,float opacity){Random random=new Random(id+(long)(position.x*100)+(long)(position.y*10)+(long)position.z);int count=phase==0?8:phase==1?6:4;var eye=camera.getPosition();for(int i=0;i<count;i++){rCol=gCol=bCol=.9F+random.nextFloat()*.1F;alpha=opacity;quadSize=scale*(random.nextFloat()*.5F+.75F);float offset=phase==0?.4F:phase==1?.6F:.9F;float x=(float)(position.x-eye.x+random.nextGaussian()*offset),y=(float)(position.y-eye.y+random.nextGaussian()*offset*.7F),z=(float)(position.z-eye.z+random.nextGaussian()*offset);renderRotatedQuad(out,camera.rotation(),x,y,z,partial);}}
        @Override protected float getU0(){return 0;}@Override protected float getU1(){return 1;}@Override protected float getV0(){return 0;}@Override protected float getV1(){return 1;}
        @Override public ParticleRenderType getRenderType(){return TYPE;}
        @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(float partial){return net.minecraft.world.phys.AABB.INFINITE;}
    }
}
