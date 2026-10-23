package com.hbm.client.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.resources.ResourceLocation;
/** Original ParticleSplash physics/lifetime/color/quarter-cycle UV flipping, using its unchanged source pixels. */
public class LegacySplashParticle extends TextureSheetParticle {
 private static final ParticleRenderType TYPE=LegacyFluidBillboardParticle.renderType(ResourceLocation.parse("hbm:textures/block/particle/particle_splash.png"));
 /** Same original layer1 lit/fog/1-over-255-alpha material as the unique tower shader. */
 protected static net.minecraft.client.renderer.ShaderInstance sourceShader(){return LegacyTowerClient.shader();}
 private static int nextId;private final int sourceId=nextId++;
 public LegacySplashParticle(ClientLevel level,double x,double y,double z,int color){super(level,x,y,z);float originalTint=1-level.random.nextFloat()*.2F;alpha=.5F;quadSize=.4F;lifetime=200+level.random.nextInt(50);gravity=.4F;hasPhysics=true;friction=.98F;float tint=1-level.random.nextFloat()*.2F;setColor((color>>16&255)/255F*tint,(color>>8&255)/255F*tint,(color&255)/255F*tint);}
 @Override public void tick(){super.tick();if(onGround)remove();else{xd+=random.nextGaussian()*.002;zd+=random.nextGaussian()*.002;if(yd<-.5)yd=-.5;}}
 @Override protected float getU0(){return sourceId%2==0?1:0;}@Override protected float getU1(){return sourceId%2==0?0:1;}@Override protected float getV0(){return sourceId%4<2?1:0;}@Override protected float getV1(){return sourceId%4<2?0:1;}
 @Override public ParticleRenderType getRenderType(){return TYPE;}
}
