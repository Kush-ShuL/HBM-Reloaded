package com.hbm.client.environment.impact;

import com.hbm.environment.impact.ImpactState;
import com.hbm.exposure.HbmExposure;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/** RenderNTMSkyboxImpact: original stars/planes/occluder/digamma/Bobmazon geometry, using native managed buffers instead of GL display lists. */
public final class ImpactSkyRenderer {
    private static final ResourceLocation SUN=ResourceLocation.withDefaultNamespace("textures/environment/sun.png"),MOON=ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png"),DIGAMMA=ResourceLocation.parse("hbm:textures/environment/impact/star_digamma.png"),BOBMAZON=ResourceLocation.parse("hbm:textures/environment/impact/sat_bobmazon.png");
    private static final List<double[]> STARS=stars();
    public static void render(ClientLevel world,float partial,Matrix4f view,Camera camera,Matrix4f projection,Runnable setupFog){
        var mc=Minecraft.getInstance();if(mc.player==null)return;var previousShader=RenderSystem.getShader();int texture=RenderSystem.getShaderTexture(0);float[] previousColor=RenderSystem.getShaderColor().clone(),previousFogColor=RenderSystem.getShaderFogColor().clone();float previousFogStart=RenderSystem.getShaderFogStart(),previousFogEnd=RenderSystem.getShaderFogEnd();var previousFogShape=RenderSystem.getShaderFogShape();
        boolean blend=GL11.glIsEnabled(GL11.GL_BLEND),depthWrite=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);int srcRGB=GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),dstRGB=GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),srcAlpha=GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),dstAlpha=GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        try{
            // DimensionSpecialEffects.renderSky returns before the native levelFogColor call.
            setupFog.run();FogRenderer.levelFogColor();float atmosphericDust=ImpactState.get(world).dust(),dust=Math.max(1-atmosphericDust*2,0),rain=dust*(1-world.getRainLevel(partial));var sky=world.getSkyColor(camera.getPosition(),partial);float r=(float)sky.x,g=(float)sky.y,b=(float)sky.z;
            var pose=new PoseStack();pose.mulPose(view);RenderSystem.depthMask(false);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.setShader(GameRenderer::getPositionColorShader);
            dome(pose,16,false,r,g,b);RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(770,771,1,0);
            float brightness=world.getStarBrightness(partial);
            if(brightness>0){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(-90));pose.mulPose(Axis.XP.rotationDegrees(world.getTimeOfDay(partial)*360));pose.mulPose(Axis.YP.rotationDegrees(-19));var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);for(var vertex:STARS)vertex(out,pose,vertex[0],vertex[1],vertex[2],brightness,brightness,brightness,brightness*rain);BufferUploader.drawWithShader(out.buildOrThrow());pose.popPose();}
            RenderSystem.blendFuncSeparate(770,1,1,0);pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(-90));pose.mulPose(Axis.XP.rotationDegrees(world.getTimeOfDay(partial)*360));
            // The original black 99.9 plane uses the same additive blend; preserve that quirk.
            colorQuad(pose,new double[][]{{-30,99.9,-30},{30,99.9,-30},{30,99.9,30},{-30,99.9,30}},0,0,0,1);
            textureQuad(pose,SUN,new double[][]{{-30,100,-30},{30,100,-30},{30,100,30},{-30,100,30}},new float[][]{{0,0},{1,0},{1,1},{0,1}},1,1,1,rain);
            int phase=world.getMoonPhase(),column=phase%4,row=phase/4%2;float u0=column/4F,v0=row/2F,u1=(column+1)/4F,v1=(row+1)/2F;
            textureQuad(pose,MOON,new double[][]{{-20,-100,20},{20,-100,20},{20,-100,-20},{-20,-100,-20}},new float[][]{{u1,v1},{u0,v1},{u0,v0},{u1,v0}},1,1,1,rain);
            float star=(float)Math.sin(world.getTimeOfDay(partial)*Math.PI);star*=star;
            pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(-90));pose.mulPose(Axis.XP.rotationDegrees(world.getTimeOfDay(partial)*360));pose.mulPose(Axis.XP.rotationDegrees(140));pose.mulPose(Axis.ZP.rotationDegrees(-40));
            float digamma=HbmExposure.digamma(mc.player),size=1*(1+digamma*.25F);double distance=100D-digamma*2.5;
            textureQuad(pose,DIGAMMA,new double[][]{{-size,distance,-size},{size,distance,-size},{size,distance,size},{-size,distance,size}},new float[][]{{0,0},{0,1},{1,1},{1,0}},star,star,star,dust);pose.popPose();
            pose.pushPose();pose.mulPose(Axis.XP.rotationDegrees(-40));pose.mulPose(Axis.YP.rotationDegrees((System.currentTimeMillis()%(360*1000))/1000F));pose.mulPose(Axis.XP.rotationDegrees((System.currentTimeMillis()%(360*100))/100F));
            textureQuad(pose,BOBMAZON,new double[][]{{-.5,100,-.5},{.5,100,-.5},{.5,100,.5},{-.5,100,.5}},new float[][]{{0,0},{0,1},{1,1},{1,0}},star,star,star,rain);pose.popPose();pose.popPose();
            RenderSystem.disableBlend();double horizon=mc.player.getEyePosition(partial).y-world.getLevelData().getHorizonHeight(world);
            if(horizon<0){pose.pushPose();pose.translate(0,12,0);dome(pose,-16,true,0,0,0);pose.popPose();float floor=-(float)(horizon+65);blackBox(pose,floor);}
            pose.pushPose();pose.translate(0,-(float)(horizon-16),0);dome(pose,-16,true,r*.2F+.04F,g*.2F+.04F,b*.6F+.1F);pose.popPose();
        }finally{
            RenderSystem.setShaderFogStart(previousFogStart);RenderSystem.setShaderFogEnd(previousFogEnd);RenderSystem.setShaderFogColor(previousFogColor[0],previousFogColor[1],previousFogColor[2],previousFogColor[3]);RenderSystem.setShaderFogShape(previousFogShape);
            RenderSystem.depthMask(depthWrite);RenderSystem.blendFuncSeparate(srcRGB,dstRGB,srcAlpha,dstAlpha);if(blend)RenderSystem.enableBlend();else RenderSystem.disableBlend();RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(previousColor[0],previousColor[1],previousColor[2],previousColor[3]);RenderSystem.setShader(()->previousShader);
        }
    }
    /** Source glSkyList/glSkyList2 run with GL_FOG enabled. Native POSITION is the
     * actual vanilla sky/fog program; POSITION_COLOR intentionally has no fog. */
    private static void dome(PoseStack pose,float y,boolean lower,float r,float g,float b){
        var shader=RenderSystem.getShader();float[] color=RenderSystem.getShaderColor().clone();
        try{
            RenderSystem.setShader(GameRenderer::getPositionShader);RenderSystem.setShaderColor(channel(r)/255F,channel(g)/255F,channel(b)/255F,1);
            var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION);
            for(int x=-384;x<=384;x+=64)for(int z=-384;z<=384;z+=64){
                if(lower){position(out,pose,x+64,y,z);position(out,pose,x,y,z);position(out,pose,x,y,z+64);position(out,pose,x+64,y,z+64);}
                else{position(out,pose,x,y,z);position(out,pose,x+64,y,z);position(out,pose,x+64,y,z+64);position(out,pose,x,y,z+64);}
            }
            BufferUploader.drawWithShader(out.buildOrThrow());
        }finally{RenderSystem.setShaderColor(color[0],color[1],color[2],color[3]);RenderSystem.setShader(()->shader);}
    }
    private static void blackBox(PoseStack pose,float floor){
        double[][] points={{-1,floor,1},{1,floor,1},{1,-1,1},{-1,-1,1},{-1,-1,-1},{1,-1,-1},{1,floor,-1},{-1,floor,-1},{1,-1,-1},{1,-1,1},{1,floor,1},{1,floor,-1},{-1,floor,-1},{-1,floor,1},{-1,-1,1},{-1,-1,-1},{-1,-1,-1},{-1,-1,1},{1,-1,1},{1,-1,-1}};
        var shader=RenderSystem.getShader();float[] color=RenderSystem.getShaderColor().clone();
        try{RenderSystem.setShader(GameRenderer::getPositionShader);RenderSystem.setShaderColor(0,0,0,1);var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION);for(var p:points)position(out,pose,p[0],p[1],p[2]);BufferUploader.drawWithShader(out.buildOrThrow());}
        finally{RenderSystem.setShaderColor(color[0],color[1],color[2],color[3]);RenderSystem.setShader(()->shader);}
    }
    private static void colorQuad(PoseStack pose,double[][] vertices,float r,float g,float b,float a){RenderSystem.setShader(GameRenderer::getPositionColorShader);var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);for(var v:vertices)vertex(out,pose,v[0],v[1],v[2],r,g,b,a);BufferUploader.drawWithShader(out.buildOrThrow());}
    private static void textureQuad(PoseStack pose,ResourceLocation texture,double[][] vertices,float[][] uv,float r,float g,float b,float a){RenderSystem.setShader(ImpactClient::skyShader);RenderSystem.setShaderTexture(0,texture);var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);for(int i=0;i<4;i++)out.addVertex(pose.last(),(float)vertices[i][0],(float)vertices[i][1],(float)vertices[i][2]).setUv(uv[i][0],uv[i][1]).setColor(channel(r),channel(g),channel(b),channel(a));BufferUploader.drawWithShader(out.buildOrThrow());}
    private static void position(VertexConsumer out,PoseStack pose,double x,double y,double z){out.addVertex(pose.last(),(float)x,(float)y,(float)z);}
    private static int channel(float value){return (int)(Math.clamp(value,0F,1F)*255);}
    private static void vertex(VertexConsumer out,PoseStack pose,double x,double y,double z,float r,float g,float b,float a){out.addVertex(pose.last(),(float)x,(float)y,(float)z).setColor(channel(r),channel(g),channel(b),channel(a));}
    private static List<double[]> stars(){
        var result=new ArrayList<double[]>();var random=new Random(10842L);
        for(int i=0;i<1500;i++){
            double d0=(double)(random.nextFloat()*2F-1F),d1=(double)(random.nextFloat()*2F-1F),d2=(double)(random.nextFloat()*2F-1F),d3=(double)(.15F+random.nextFloat()*.1F),d4=d0*d0+d1*d1+d2*d2;
            if(d4<1D&&d4>.01D){d4=1D/Math.sqrt(d4);d0*=d4;d1*=d4;d2*=d4;double d5=d0*100D,d6=d1*100D,d7=d2*100D,d8=Math.atan2(d0,d2),d9=Math.sin(d8),d10=Math.cos(d8),d11=Math.atan2(Math.sqrt(d0*d0+d2*d2),d1),d12=Math.sin(d11),d13=Math.cos(d11),d14=random.nextDouble()*Math.PI*2D,d15=Math.sin(d14),d16=Math.cos(d14);
                for(int j=0;j<4;j++){double d17=0,d18=(double)((j&2)-1)*d3,d19=(double)((j+1&2)-1)*d3,d20=d18*d16-d19*d15,d21=d19*d16+d18*d15,d22=d20*d12+d17*d13,d23=d17*d12-d20*d13,d24=d23*d9-d21*d10,d25=d21*d9+d23*d10;result.add(new double[]{d5+d24,d6+d22,d7+d25});}
            }
        }return List.copyOf(result);
    }
    private ImpactSkyRenderer(){}
}
