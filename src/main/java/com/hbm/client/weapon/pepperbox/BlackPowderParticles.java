package com.hbm.client.weapon.pepperbox;

import com.hbm.weapon.sedna.gun.GunNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.awt.Color;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;

/** Actual BlackPowderCreator, ParticleBlackPowderSmoke/Spark and EntityFXRotating path. */
public final class BlackPowderParticles {
    private static final Random EFFECT_RANDOM=new Random();
    private static final ResourceLocation CLOUD=ResourceLocation.parse("hbm:textures/particle/black_powder_cloud.png"),SPARK=ResourceLocation.parse("hbm:textures/particle/legacy_particles.png");
    private static final ParticleRenderType SMOKE_TYPE=type(CLOUD),SPARK_TYPE=type(SPARK);
    private static int serial;
    private static ParticleRenderType type(ResourceLocation texture){return new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GunEffectsClient::particleShader);RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();if(com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(770,771,1,771);else RenderSystem.defaultBlendFunc();RenderSystem.disableCull();RenderSystem.enableDepthTest();RenderSystem.depthFunc(515);RenderSystem.depthMask(false);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}
    };}
    public static void receive(GunNetwork.BlackPowder packet){
        var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;
        double hx=packet.hx(),hy=packet.hy(),hz=packet.hz(),length=(double)(float)Math.sqrt(hx*hx+hy*hy+hz*hz);
        if(length<1E-4){hx=hy=hz=0;}else{double inverse=1D/length;hx*=inverse;hy*=inverse;hz*=inverse;}
        for(int i=0;i<packet.clouds();i++){
            var cloud=new Smoke(level,packet.x(),packet.y(),packet.z(),packet.scale());
            double speed=.85D+EFFECT_RANDOM.nextDouble()*.3D;
            cloud.motion(hx*packet.cloudSpeed()*speed+EFFECT_RANDOM.nextGaussian()*.05D,hy*packet.cloudSpeed()*speed+EFFECT_RANDOM.nextGaussian()*.05D,hz*packet.cloudSpeed()*speed+EFFECT_RANDOM.nextGaussian()*.05D);
            mc.particleEngine.add(cloud);
        }
        for(int i=0;i<packet.sparks();i++){
            double speed=.85D+EFFECT_RANDOM.nextDouble()*.3D;
            mc.particleEngine.add(new Spark(level,packet.x(),packet.y(),packet.z(),hx*packet.sparkSpeed()*speed+EFFECT_RANDOM.nextGaussian()*.02D,hy*packet.sparkSpeed()*speed+EFFECT_RANDOM.nextGaussian()*.02D,hz*packet.sparkSpeed()*speed+EFFECT_RANDOM.nextGaussian()*.02D));
        }
    }
    private abstract static class SourceParticle extends Particle {
        final Random sourceRandom=new Random();final int identity=serial++;float sourceScale;
        SourceParticle(ClientLevel level,double x,double y,double z){
            super(level,x,y,z);setSize(.2F,.2F);setBoundingBox(getBoundingBox().move(0,-(double).1F,0));
            // Original EntityFX first samples its two unused texture jitters, then its scale and default life.
            sourceRandom.nextFloat();sourceRandom.nextFloat();sourceScale=(sourceRandom.nextFloat()*.5F+.5F)*2F;lifetime=(int)(4F/(sourceRandom.nextFloat()*.9F+.1F));
        }
        @Override public void move(double dx,double dy,double dz){
            var motion=hasPhysics?Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of()):new Vec3(dx,dy,dz);
            setBoundingBox(getBoundingBox().move(motion));setLocationFromBoundingbox();onGround=dy!=motion.y&&dy<0;if(dx!=motion.x)xd=0;if(dy!=motion.y)yd=0;if(dz!=motion.z)zd=0;
        }
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
        @Override protected int getLightColor(float partial){var pos=BlockPos.containing(x,y+(double)(bbHeight*.66F)-(double).1F,z);return level.hasChunkAt(pos)?LevelRenderer.getLightColor(level,pos):0;}
        @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
        final void billboard(VertexConsumer out,Camera camera,float partial,double scale,float rotation,float u0,float u1,float v0,float v1,float opacity){
            var eye=camera.getPosition();float px=(float)(xo+(x-xo)*partial-eye.x),py=(float)(yo+(y-yo)*partial-eye.y),pz=(float)(zo+(z-zo)*partial-eye.z);
            float yaw=camera.getYRot()*(float)Math.PI/180F,pitch=camera.getXRot()*(float)Math.PI/180F,sx=Mth.cos(yaw),sz=Mth.sin(yaw),sy=Mth.cos(pitch),dx=-sz*Mth.sin(pitch),dz=sx*Mth.sin(pitch);
            double[][] vertices={{-sx*scale-dx*scale,-sy*scale,-sz*scale-dz*scale},{-sx*scale+dx*scale,sy*scale,-sz*scale+dz*scale},{sx*scale+dx*scale,sy*scale,sz*scale+dz*scale},{sx*scale-dx*scale,-sy*scale,sz*scale-dz*scale}};
            double ax=vertices[1][0]-vertices[0][0],ay=vertices[1][1]-vertices[0][1],az=vertices[1][2]-vertices[0][2],bx=vertices[2][0]-vertices[0][0],by=vertices[2][1]-vertices[0][1],bz=vertices[2][2]-vertices[0][2];
            double nx=ay*bz-az*by,ny=az*bx-ax*bz,nz=ax*by-ay*bx,len=(double)(float)Math.sqrt(nx*nx+ny*ny+nz*nz);
            if(len<1E-4){nx=ny=nz=0;}else{double inverse=1D/len;nx*=inverse;ny*=inverse;nz*=inverse;}
            double cosine=Math.cos(rotation*Math.PI/180D),sine=Math.sin(rotation*Math.PI/180D);float[] us={u1,u1,u0,u0},vs={v1,v0,v0,v1};int light=getLightColor(partial);
            for(int i=0;i<4;i++){double vx=vertices[i][0],vy=vertices[i][1],vz=vertices[i][2];out.addVertex((float)(px+vx*cosine+(ny*vz-nz*vy)*sine),(float)(py+vy*cosine+(nz*vx-nx*vz)*sine),(float)(pz+vz*cosine+(nx*vy-ny*vx)*sine)).setUv(us[i],vs[i]).setColor(rCol,gCol,bCol,opacity).setLight(light);}
        }
    }
    private static final class Smoke extends SourceParticle {
        final float hue;float rotation,previousRotation;
        void motion(double x,double y,double z){xd=x;yd=y;zd=z;}
        Smoke(ClientLevel level,double x,double y,double z,float scale){super(level,x,y,z);lifetime=30+sourceRandom.nextInt(15);sourceScale=scale*.9F+sourceRandom.nextFloat()*.2F;gravity=0;hue=20F+sourceRandom.nextFloat()*20F;Color color=Color.getHSBColor(hue/255F,1,1);rCol=color.getRed()/255F;gCol=color.getGreen()/255F;bCol=color.getBlue()/255F;hasPhysics=false;}
        @Override public void tick(){xo=x;yo=y;zo=z;if(++age>=lifetime)remove();yd-=gravity;previousRotation=rotation;float scaled=(float)age/lifetime;rotation+=(1-scaled)*2*((identity%2)-.5);xd*=.65D;yd*=.65D;zd*=.65D;move(xd,yd,zd);}
        @Override public void render(VertexConsumer out,Camera camera,float partial){if(com.hbm.client.bomb.nuclear.MukeRenderTypes.defer(this,partial))return;out=com.hbm.client.bomb.nuclear.MukeRenderTypes.begin(SMOKE_TYPE);double scaled=(age+partial)/(double)lifetime;var color=Color.getHSBColor(hue/255F,Math.max(1F-(float)scaled*4F,0),Mth.clamp(1.25F-(float)scaled*2F,.7F,1F));rCol=color.getRed()/255F;gCol=color.getGreen()/255F;bCol=color.getBlue()/255F;alpha=(float)Math.pow(1-Math.min(scaled,1),.25D);double scale=(.25D+scaled+(age+partial)*.025D)*sourceScale;billboard(out,camera,partial,scale,previousRotation+(rotation-previousRotation)*partial,0,1,0,1,alpha*.25F);}
        @Override public ParticleRenderType getRenderType(){return com.hbm.client.bomb.nuclear.MukeRenderTypes.ORDERED;}
    }
    private static final class Spark extends SourceParticle {
        Spark(ClientLevel level,double x,double y,double z,double vx,double vy,double vz){
            super(level,x,y,z);
            // EntityFX's velocity ctor consumes five Math.random calls and computes a throwaway random heading before source overwrites motion.
            double tx=vx+(float)(Math.random()*2D-1D)*.4F,ty=vy+(float)(Math.random()*2D-1D)*.4F,tz=vz+(float)(Math.random()*2D-1D)*.4F;float unusedSpeed=(float)(Math.random()+Math.random()+1D)*.15F;
            xd=vx;yd=vy;zd=vz;float tint=sourceRandom.nextFloat()*.1F+.2F;rCol=tint+.7F;gCol=tint+.5F;bCol=tint;
            var box=getBoundingBox();bbWidth=bbHeight=.02F;setBoundingBox(new AABB(box.minX,box.minY,box.minZ,box.minX+.02F,box.minY+.02F,box.minZ+.02F));sourceScale*=sourceRandom.nextFloat()*.6F+.5F;lifetime=15+sourceRandom.nextInt(5);gravity=.01F;
        }
        @Override public void tick(){xo=x;yo=y;zo=z;yd-=gravity;move(xd,yd,zd);xd*=.95D;yd*=.95D;zd*=.95D;if(lifetime--<=0)remove();}
        @Override public void render(VertexConsumer out,Camera camera,float partial){if(com.hbm.client.bomb.nuclear.MukeRenderTypes.defer(this,partial))return;out=com.hbm.client.bomb.nuclear.MukeRenderTypes.begin(SPARK_TYPE);billboard(out,camera,partial,.1F*sourceScale,0,0,.0624375F,0,.0624375F,1F);}
        @Override protected int getLightColor(float partial){return 15728880;}
        @Override public ParticleRenderType getRenderType(){return com.hbm.client.bomb.nuclear.MukeRenderTypes.ORDERED;}
    }
    private BlackPowderParticles(){}
}
