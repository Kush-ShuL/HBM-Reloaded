package com.hbm.client.bomb.conventional.grenade;

import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.List;
import java.util.Random;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;

/** Original ParticleHaze and official 1.7.10 EntityFlameFX/EntityLavaFX, using the untouched old particle sheet. */
public final class GrenadeParticleEffects {
 private GrenadeParticleEffects(){}
 private static final ParticleRenderType SPRITE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/particle/vanilla_1_7_10.png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.depthMask(true);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
 private static final ParticleRenderType HAZE=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/particle/haze.png"));
 public static void flame(Level level,double x,double y,double z){if(level instanceof ClientLevel client)Minecraft.getInstance().particleEngine.add(new OldFlame(client,x,y,z));}
 public static void haze(double x,double y,double z){var mc=Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new Haze(mc.level,x,y,z));}
 private abstract static class OldSprite extends SingleQuadParticle{
  final int index;float originalSize;
  OldSprite(ClientLevel level,double x,double y,double z,int index){super(level,x,y,z);this.index=index;random.nextFloat();random.nextFloat();quadSize=(random.nextFloat()*.5F+.5F)*2F*.1F;originalSize=quadSize;setBoundingBox(getBoundingBox().move(0,-(double).1F,0));}
  void motion(){xd=(double)((float)(Math.random()*2-1)*.4F);yd=(double)((float)(Math.random()*2-1)*.4F);zd=(double)((float)(Math.random()*2-1)*.4F);float speed=(float)(Math.random()+Math.random()+1)*.15F,norm=(float)Math.sqrt(xd*xd+yd*yd+zd*zd);xd=xd/norm*speed*(double).4F;yd=yd/norm*speed*(double).4F+(double).1F;zd=zd/norm*speed*(double).4F;}
  @Override public void move(double dx,double dy,double dz){var motion=hasPhysics?Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of()):new Vec3(dx,dy,dz);setBoundingBox(getBoundingBox().move(motion));setLocationFromBoundingbox();onGround=dy!=motion.y&&dy<0;if(dx!=motion.x)xd=0;if(dy!=motion.y)yd=0;if(dz!=motion.z)zd=0;}
  @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
  @Override protected float getU0(){return (index%16)/16F;}@Override protected float getU1(){return getU0()+.0624375F;}@Override protected float getV0(){return (index/16)/16F;}@Override protected float getV1(){return getV0()+.0624375F;}@Override public ParticleRenderType getRenderType(){return SPRITE;}
 }
 private static final class OldFlame extends OldSprite{
  OldFlame(ClientLevel level,double x,double y,double z){super(level,x,y,z,48);motion();xd*=(double).01F;yd*=(double).01F;zd*=(double).01F;for(int i=0;i<6;i++)random.nextFloat();lifetime=(int)(8/(Math.random()*.8+.2))+4;hasPhysics=false;}
  @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();move(xd,yd,zd);xd*=(double).96F;yd*=(double).96F;zd*=(double).96F;if(onGround){xd*=(double).7F;zd*=(double).7F;}}
  @Override public float getQuadSize(float partial){float f=(age+partial)/lifetime;return originalSize*(1-f*f*.5F);}
  @Override protected int getLightColor(float partial){int light=super.getLightColor(partial),block=(light&255)+(int)(Mth.clamp((age+partial)/lifetime,0,1)*15*16);return Math.min(block,240)|(light&0xFF0000);}
 }
 private static final class OldLava extends OldSprite{
  OldLava(ClientLevel level,double x,double y,double z){super(level,x,y,z,49);motion();xd*=(double).8F;yd=random.nextFloat()*.4F+.05F;zd*=(double).8F;quadSize*=random.nextFloat()*2+.2F;originalSize=quadSize;lifetime=(int)(16/(Math.random()*.8+.2));}
  @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();float fraction=(float)age/lifetime;if(random.nextFloat()>fraction)Minecraft.getInstance().particleEngine.add(LegacyExplosionEffects.create("smoke",level,x,y,z,xd,yd,zd));yd-=.03;move(xd,yd,zd);xd*=(double).999F;yd*=(double).999F;zd*=(double).999F;if(onGround){xd*=(double).7F;zd*=(double).7F;}}
  @Override public float getQuadSize(float partial){float f=(age+partial)/lifetime;return originalSize*(1-f*f);}
  @Override protected int getLightColor(float partial){return(super.getLightColor(partial)&0xFF0000)|240;}
 }
 private static final class Haze extends Particle{
  Haze(ClientLevel level,double x,double y,double z){super(level,x,y,z);random.nextFloat();random.nextFloat();random.nextFloat();lifetime=600+random.nextInt(100);}
  @Override public void tick(){xo=x;yo=y;zo=z;if(++age>=lifetime)remove();xd*=(double).96F;yd*=(double).96F;zd*=(double).96F;if(onGround){xd*=(double).7F;zd*=(double).7F;}int px=Mth.floor(x)+random.nextInt(15)-7,pz=Mth.floor(z)+random.nextInt(15)-7,py=level.getHeight(Heightmap.Types.MOTION_BLOCKING,px,pz);Minecraft.getInstance().particleEngine.add(new OldLava(level,px+random.nextDouble(),py+.1,pz+random.nextDouble()));}
  @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;out=MukeRenderTypes.begin(HAZE);float alpha=(float)Math.sin(age*Math.PI/400F)*.25F*.1F;var eye=camera.getPosition();var random=new Random(50);float tx=0,ty=0,tz=0;float yaw=camera.getYRot()*(float)Math.PI/180F,pitch=camera.getXRot()*(float)Math.PI/180F,sx=Mth.cos(yaw),sz=Mth.sin(yaw),dx=-sz*Mth.sin(pitch),dz=sx*Mth.sin(pitch),sy=Mth.cos(pitch);
   for(int i=0;i<25;i++){tx+=(float)(random.nextGaussian()*2.5);ty+=(float)(random.nextGaussian()*.15);tz+=(float)(random.nextGaussian()*2.5);double size=(random.nextDouble()*.25+.75)*10;float px=(float)(Mth.lerp(partial,xo,x)-eye.x+random.nextGaussian()*.5)+tx,py=(float)(Mth.lerp(partial,yo,y)-eye.y+random.nextGaussian()*.5)+ty,pz=(float)(Mth.lerp(partial,zo,z)-eye.z+random.nextGaussian()*.5)+tz;
    vertex(out,px-(float)((sx+dx)*size),py-(float)(sy*size),pz-(float)((sz+dz)*size),1,1,alpha);vertex(out,px+(float)((-sx+dx)*size),py+(float)(sy*size),pz+(float)((-sz+dz)*size),1,0,alpha);vertex(out,px+(float)((sx+dx)*size),py+(float)(sy*size),pz+(float)((sz+dz)*size),0,0,alpha);vertex(out,px+(float)((sx-dx)*size),py-(float)(sy*size),pz+(float)((sz-dz)*size),0,1,alpha);}}
  private static void vertex(VertexConsumer out,float x,float y,float z,float u,float v,float alpha){out.addVertex(x,y,z).setUv(u,v).setColor(1,1,1,Math.max(alpha,0)).setLight(240);}
  @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}@Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
 }
}
