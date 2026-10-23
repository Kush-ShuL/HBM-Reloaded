package com.hbm.client.drain;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.hbm.client.particle.LegacyCoolingTowerParticle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/** Drain-only material/ordering adapter. All source scale, alpha, drift, lifetime and motion remain inherited. */
public final class DrainSmokeParticle extends LegacyCoolingTowerParticle {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/block/particle/particle_base.png");
    private static final Tesselator TESSELLATOR=new Tesselator(4096);
    private static final ArrayList<Draw> PENDING=new ArrayList<>();
    private static boolean queued;
    private record Draw(DrainSmokeParticle particle,Camera camera,float partial,double distance){}
    public DrainSmokeParticle(ClientLevel level,double x,double y,double z){super(level,x,y,z);}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    @Override public void render(VertexConsumer ignored,Camera camera,float partial){
        var eye=camera.getPosition();double dx=Mth.lerp(partial,xo,x)-eye.x,dy=Mth.lerp(partial,yo,y)-eye.y,dz=Mth.lerp(partial,zo,z)-eye.z;
        PENDING.add(new Draw(this,camera,partial,dx*dx+dy*dy+dz*dz));
        // One local batch joins the unique global smoke/cloud-split queue; no second world renderer.
        if(!queued){queued=true;MukeRenderTypes.deferDraw(DrainSmokeParticle::drawPending);}
    }
    static void resetFrame(){PENDING.clear();queued=false;}
    private void sourceBillboard(VertexConsumer out,Camera camera,float partial){billboard(out,camera,partial,particleScale,0,1,0,1);}
    private static void drawPending(){
        if(PENDING.isEmpty())return;
        PENDING.sort(Comparator.comparingDouble(Draw::distance).reversed());
        State previous=State.capture();BufferBuilder vertices=null;
        try{
            // Existing tower shader retains lit/fogged source pixels and the original >1/255 alpha test.
            RenderSystem.setShader(DrainSmokeParticle::sourceShader);RenderSystem.setShaderTexture(0,TEXTURE);RenderSystem.setShaderColor(1,1,1,1);
            RenderSystem.enableBlend();
            if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ONE_MINUS_SRC_ALPHA);
            else RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();RenderSystem.depthFunc(GL11.GL_LEQUAL);RenderSystem.depthMask(true);RenderSystem.disableCull();
            vertices=TESSELLATOR.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);
            for(var draw:PENDING)draw.particle().sourceBillboard(vertices,draw.camera(),draw.partial());
            var mesh=vertices.build();vertices=null;if(mesh!=null)BufferUploader.drawWithShader(mesh);
        }finally{
            try{if(vertices!=null){var mesh=vertices.build();if(mesh!=null)mesh.close();}}finally{previous.restore();}
        }
        // Keep the same batch for the shared queue's behind-cloud / Fabulous depth replay. Next frame clears it.
    }
    /** Restore every render state changed here, including its exact prior depth-write state. */
    private record State(boolean depthWrite,boolean depthTest,boolean blend,boolean cull,int depthFunction,
                         int sourceRgb,int targetRgb,int sourceAlpha,int targetAlpha,
                         ShaderInstance shader,int texture,float[] color){
        static State capture(){return new State(GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),GL11.glIsEnabled(GL11.GL_DEPTH_TEST),GL11.glIsEnabled(GL11.GL_BLEND),GL11.glIsEnabled(GL11.GL_CULL_FACE),GL11.glGetInteger(GL11.GL_DEPTH_FUNC),GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA),RenderSystem.getShader(),RenderSystem.getShaderTexture(0),RenderSystem.getShaderColor().clone());}
        void restore(){
            RenderSystem.depthMask(depthWrite);RenderSystem.depthFunc(depthFunction);
            if(depthTest)RenderSystem.enableDepthTest();else RenderSystem.disableDepthTest();
            RenderSystem.blendFuncSeparate(sourceRgb,targetRgb,sourceAlpha,targetAlpha);
            if(blend)RenderSystem.enableBlend();else RenderSystem.disableBlend();
            if(cull)RenderSystem.enableCull();else RenderSystem.disableCull();
            RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(color[0],color[1],color[2],color[3]);RenderSystem.setShader(()->shader);
        }
    }
}
