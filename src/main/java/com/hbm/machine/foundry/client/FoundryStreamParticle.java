package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.FoundryStreamPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Exact source ParticleFoundry 20-tick mesh, widths, texture scroll and brightening. */
public final class FoundryStreamParticle extends Particle {
 private final int color;private final Direction dir;private final double length,base,offset;
 private static final ParticleRenderType TYPE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.fromNamespaceAndPath("hbm","textures/block/lava_gray.png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.disableBlend();RenderSystem.disableCull();RenderSystem.depthMask(true);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
 public FoundryStreamParticle(ClientLevel level,FoundryStreamPacket packet){super(level,packet.x(),packet.y(),packet.z());color=packet.color();dir=packet.direction();length=packet.length();base=packet.base();offset=packet.offset();lifetime=20;}
 @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();}
 @Override public void render(VertexConsumer out,Camera camera,float interp){var eye=camera.getPosition();float pX=(float)(Mth.lerp(interp,xo,x)-eye.x),pY=(float)(Mth.lerp(interp,yo,y)-eye.y),pZ=(float)(Mth.lerp(interp,zo,z)-eye.z);
        int rgb=FoundryStreamGeometry.brightenedRgb(color);
        rCol=(rgb>>16&255)/255F;gCol=(rgb>>8&255)/255F;bCol=(rgb&255)/255F;
        double scroll=(int)(System.currentTimeMillis()/100%16)/16D;
        FoundryStreamGeometry.emit(dir,length,base,offset,age+interp,lifetime,scroll,
                (vx,vy,vz,u,v)->vertex(out,pX,pY,pZ,vx,vy,vz,u,v));
 }

 private void vertex(VertexConsumer out,float px,float py,float pz,double x,double y,double z,double u,double v){out.addVertex((float)x+px,(float)y+py,(float)z+pz).setUv((float)u,(float)v).setColor(rCol,gCol,bCol,1F).setLight(240);}
 @Override public AABB getRenderBoundingBox(float partial){double extent=offset+.25;return new AABB(x-extent,y-length,z-extent,x+extent,y+base+.25,z+extent);}
 @Override public ParticleRenderType getRenderType(){return TYPE;}
}
