package com.hbm.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

abstract class LegacyFluidBillboardParticle extends Particle {
    protected float particleScale;
    protected LegacyFluidBillboardParticle(ClientLevel level,double x,double y,double z){super(level,x,y,z);particleScale=(random.nextFloat()*.5F+.5F)*2;lifetime=(int)(4F/(random.nextFloat()*.9F+.1F));hasPhysics=false;}
    @Override public void move(double dx,double dy,double dz){setPos(x+dx,y+dy,z+dz);}
    protected void billboard(VertexConsumer buffer,Camera camera,float partial,float scale,float minU,float maxU,float minV,float maxV){
        var eye=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);int light=getLightColor(partial);
        var corners=new Vector3f[]{new Vector3f(-1,-1,0),new Vector3f(-1,1,0),new Vector3f(1,1,0),new Vector3f(1,-1,0)};float[] us={maxU,maxU,minU,minU},vs={maxV,minV,minV,maxV};
        for(int i=0;i<4;i++){var corner=corners[i].rotate(camera.rotation()).mul(scale).add(px,py,pz);buffer.addVertex(corner.x,corner.y,corner.z).setUv(us[i],vs[i]).setColor(rCol,gCol,bCol,alpha).setLight(light);}
    }
    protected static ParticleRenderType renderType(ResourceLocation texture){return new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.disableCull();RenderSystem.depthMask(false);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}
        @Override public String toString(){return "HBM legacy fluid particle "+texture;}
    };}
}
