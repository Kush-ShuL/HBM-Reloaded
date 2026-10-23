package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** Original ParticleDeadLeaf scale, motion, 200–249 tick life and four alternating UV orientations. */
public final class NuclearDeadLeaf extends SingleQuadParticle {
 private static int serial;
 private final int orientation=serial++;
 private static final ParticleRenderType TYPE=new ParticleRenderType(){
  @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.depthMask(true);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.fromNamespaceAndPath("hbm","textures/block/particle/dead_leaf.png"));return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}
 };
 private NuclearDeadLeaf(ClientLevel level,double x,double y,double z){super(level,x,y,z);random.nextFloat();random.nextFloat();random.nextFloat();rCol=gCol=bCol=1F-level.random.nextFloat()*.2F;quadSize=.1F;lifetime=200+level.random.nextInt(50);}
 public static void spawn(Level level,double x,double y,double z){if(level instanceof ClientLevel client)Minecraft.getInstance().particleEngine.add(new NuclearDeadLeaf(client,x,y,z));}
 @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04D*(double).2F;move(xd,yd,zd);xd*=(double).98F;yd*=(double).98F;zd*=(double).98F;if(onGround){xd*=(double).7F;zd*=(double).7F;}else{xd+=random.nextGaussian()*.002D;zd+=random.nextGaussian()*.002D;if(yd<-.025D)yd=-.025D;}}
 @Override protected float getU0(){return orientation%2==0?1:0;}
 @Override protected float getU1(){return orientation%2==0?0:1;}
 @Override protected float getV0(){return orientation%4<2?1:0;}
 @Override protected float getV1(){return orientation%4<2?0:1;}
 @Override public ParticleRenderType getRenderType(){return TYPE;}
}
