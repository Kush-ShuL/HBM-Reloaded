package com.hbm.client.particle;

import com.hbm.client.bomb.nuclear.MukeClient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/** Original additive Hadron flash; the shared Muke shader preserves the source alpha>0 cutoff. */
public final class LegacyHadronParticle extends LegacyFluidBillboardParticle {
    private static final ParticleRenderType TYPE=new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(MukeClient::additiveShader);RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/particle/hadron.png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();if(com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO,GL11.GL_ONE);else RenderSystem.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);RenderSystem.disableCull();RenderSystem.enableDepthTest();RenderSystem.depthFunc(515);RenderSystem.depthMask(false);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);}
        @Override public String toString(){return "HBM legacy hadron";}
    };
    public LegacyHadronParticle(ClientLevel level,double x,double y,double z){super(level,x,y,z);lifetime=10;particleScale=1;}
    public LegacyHadronParticle makeSmall(boolean small){if(small){particleScale=.5F;lifetime=5;}return this;}
    @Override public void render(VertexConsumer out,Camera camera,float partial){if(com.hbm.client.bomb.nuclear.MukeRenderTypes.defer(this,partial))return;out=com.hbm.client.bomb.nuclear.MukeRenderTypes.begin(TYPE);var eye=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);alpha=1-(age+partial)/lifetime;float scale=(age+partial)*.15F*particleScale;var corners=new Vector3f[]{new Vector3f(-1,-1,0),new Vector3f(-1,1,0),new Vector3f(1,1,0),new Vector3f(1,-1,0)};float[] us={1,1,0,0},vs={1,0,0,1};for(int i=0;i<4;i++){var corner=corners[i].rotate(camera.rotation()).mul(scale).add(px,py,pz);out.addVertex(corner.x,corner.y,corner.z).setUv(us[i],vs[i]).setColor(1,1,1,alpha);}}
    @Override public ParticleRenderType getRenderType(){return com.hbm.client.bomb.nuclear.MukeRenderTypes.ORDERED;}
}
