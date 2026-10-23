package com.hbm.machine.reactor.zirnox.client;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL11;
/** Exact stationary ParticleRBMKFlame lifetime, source five-frame indexing and yaw-only billboard. */
public final class ZirnoxFlameParticle extends Particle {
    private final float scale;
    public ZirnoxFlameParticle(ClientLevel level,double x,double y,double z,int maxAge){super(level,x,y,z);lifetime=maxAge;scale=random.nextFloat()+1;hasPhysics=false;}
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();}
    @Override public void render(VertexConsumer buffer,Camera camera,float partial){if(com.hbm.client.bomb.nuclear.MukeRenderTypes.defer(this,partial))return;buffer=com.hbm.client.bomb.nuclear.MukeRenderTypes.begin(TYPE);float current=Math.min(age,lifetime),fade=1;if(current<20)fade=current/20;if(current>lifetime-20)fade=(lifetime-current)/20;fade*=.5F;int texture=Math.min(age,lifetime)*5%14;float u=(texture%5)/14F;var eye=camera.getPosition();float yaw=(float)Math.toRadians(camera.getYRot()),pitch=(float)Math.toRadians(camera.getXRot()),cos=(float)Math.cos(yaw),sin=(float)Math.sin(yaw);float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y)+(float)Math.cos(pitch),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);float[] hs={-scale,-scale,scale,scale},vs={-2*scale,2*scale,2*scale,-2*scale},us={u+1/14F,u+1/14F,u,u},ts={1,0,0,1};for(int i=0;i<4;i++)buffer.addVertex(px+hs[i]*cos,py+vs[i],pz+hs[i]*sin).setUv(us[i],ts[i]).setColor(1F,1F,1F,fade);}
    private static final ParticleRenderType TYPE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(()->{com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.setupShader(ZirnoxClient.flameShader);return ZirnoxClient.flameShader;});RenderSystem.setShaderTexture(0,ZirnoxRenderer.id("textures/particle/rbmk_fire.png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();if(com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO,GL11.GL_ONE);else RenderSystem.blendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);RenderSystem.disableCull();RenderSystem.enableDepthTest();RenderSystem.depthFunc(515);RenderSystem.depthMask(false);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);}@Override public String toString(){return "HBM RBMK stationary flame";}};
    @Override public ParticleRenderType getRenderType(){return com.hbm.client.bomb.nuclear.MukeRenderTypes.ORDERED;}
}
