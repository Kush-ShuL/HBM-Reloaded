package com.hbm.client.drain;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.particle.LegacySplashParticle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
/** Liquid drain branch: unchanged source splash physics/UV/colour, with the existing cloud split. */
public final class DrainSplashParticle extends LegacySplashParticle {
 private static final ParticleRenderType SOURCE=new ParticleRenderType(){
  private final ParticleRenderType colour=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/block/particle/particle_splash.png"),false);
  @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){var out=colour.begin(tess,textures);RenderSystem.setShader(DrainSplashParticle::sourceShader);RenderSystem.depthMask(true);return out;}
 };
 public DrainSplashParticle(ClientLevel level,double x,double y,double z,int colour){super(level,x,y,z,colour);}
 @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
 @Override public void render(VertexConsumer ignored,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;super.render(MukeRenderTypes.begin(SOURCE),camera,partial);}
}
