package com.hbm.client.entity.crab;
import com.hbm.entity.mob.crab.CyberTauBullet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.List;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;
/** Tau's original eight reddust points: real old EntityReddustFX and particle sheet, not modern dust artwork. */
public final class CrabParticles {
 private CrabParticles(){}
 public static void trail(CyberTauBullet bullet,Vec3 pos,boolean tau){var mc=Minecraft.getInstance();if(!(bullet.level() instanceof ClientLevel level)||mc.getCameraEntity()==null||mc.getCameraEntity().distanceToSqr(pos)>256||!tau)return;var setting=mc.options.particles().get();if(setting==ParticleStatus.MINIMAL||setting==ParticleStatus.DECREASED&&level.random.nextInt(3)==0)return;mc.particleEngine.add(new Dust(level,pos));}
 public static void colored(ClientLevel level,Vec3 pos,float red,float green,float blue){Minecraft.getInstance().particleEngine.add(new Dust(level,pos,red,green,blue));}
 public static void colored(ClientLevel level,Vec3 pos,Vec3 velocity,float red,float green,float blue){var particle=new Dust(level,pos,red,green,blue);particle.setParticleSpeed(velocity.x,velocity.y,velocity.z);Minecraft.getInstance().particleEngine.add(particle);}
 private static final ParticleRenderType MATERIAL=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/particle/vanilla_1_7_10.png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.depthMask(true);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
 private static final class Dust extends SingleQuadParticle{
  final float originalSize;int texture;
  Dust(ClientLevel level,Vec3 pos){this(level,pos,1,0,0);}
  Dust(ClientLevel level,Vec3 pos,float red,float green,float blue){super(level,pos.x,pos.y,pos.z);random.nextFloat();random.nextFloat();quadSize=(random.nextFloat()*.5F+.5F)*2*.1F*.75F;originalSize=quadSize;setBoundingBox(getBoundingBox().move(0,-(double).1F,0));xd=(double)((float)(Math.random()*2-1)*.4F);yd=(double)((float)(Math.random()*2-1)*.4F);zd=(double)((float)(Math.random()*2-1)*.4F);float speed=(float)(Math.random()+Math.random()+1)*.15F,norm=(float)Math.sqrt(xd*xd+yd*yd+zd*zd);xd=xd/norm*speed*(double).4F*(double).1F;yd=(yd/norm*speed*(double).4F+(double).1F)*(double).1F;zd=zd/norm*speed*(double).4F*(double).1F;float tint=(float)Math.random()*.4F+.6F;if(red==0)red=1;rCol=((float)(Math.random()*(double).2F)+.8F)*red*tint;gCol=((float)(Math.random()*(double).2F)+.8F)*green*tint;bCol=((float)(Math.random()*(double).2F)+.8F)*blue*tint;lifetime=(int)(8/(Math.random()*.8+.2));}
  @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();texture=7-age*8/lifetime;move(xd,yd,zd);if(y==yo){xd*=1.1;zd*=1.1;}xd*=(double).96F;yd*=(double).96F;zd*=(double).96F;if(onGround){xd*=(double).7F;zd*=(double).7F;}}
  @Override public void move(double x,double y,double z){var motion=Entity.collideBoundingBox(null,new Vec3(x,y,z),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(motion));setLocationFromBoundingbox();onGround=y!=motion.y&&y<0;if(x!=motion.x)xd=0;if(y!=motion.y)yd=0;if(z!=motion.z)zd=0;}
  @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
  @Override public float getQuadSize(float partial){return originalSize*Mth.clamp((age+partial)/lifetime*32,0,1);}@Override protected float getU0(){return texture%16/16F;}@Override protected float getU1(){return getU0()+.0624375F;}@Override protected float getV0(){return texture/16/16F;}@Override protected float getV1(){return getV0()+.0624375F;}@Override public ParticleRenderType getRenderType(){return MATERIAL;}
 }
}
