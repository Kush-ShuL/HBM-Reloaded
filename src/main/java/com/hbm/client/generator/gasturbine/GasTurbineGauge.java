package com.hbm.client.generator.gasturbine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Source GUIElements.drawSmoothTextureModalCircle: its exact clipped triangle fan, not a replacement arc. */
final class GasTurbineGauge {
    static void render(GuiGraphics graphics,ResourceLocation texture,int x,int y,double progress){progress=Math.max(0,Math.min(1,progress));float angle=(float)(-progress*270);double theta=Math.toRadians(angle-135);int addons=angle>=-180&&angle<-90?1:angle>=-270&&angle<-180?2:0;double tx=0,ty=0;if(angle>=-90){tx=-1;ty=-Math.tan(theta);}else if(angle>-135&&angle<-90){tx=Math.tan(Math.PI/2-theta);ty=1;}else if(angle>-180&&angle<-135){tx=Math.tan(Math.PI/2-theta);ty=1;}else if(angle<=-180){tx=1;ty=Math.tan(theta);}else if(angle==-135){tx=0;ty=1;}tx*=24;ty*=24;graphics.flush();RenderSystem.setShader(GameRenderer::getPositionTexShader);RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(1,1,1,1);var builder=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_TEX);var matrix=graphics.pose().last().pose();v(builder,matrix,x,y,0,48);v(builder,matrix,x,y,24,24);if(addons>=1){v(builder,matrix,x,y,0,0);v(builder,matrix,x,y,0,0);v(builder,matrix,x,y,24,24);}if(addons==2){v(builder,matrix,x,y,48,0);v(builder,matrix,x,y,48,0);v(builder,matrix,x,y,24,24);}v(builder,matrix,x,y,tx+24,24-ty);BufferUploader.drawWithShader(builder.buildOrThrow());}
    private static void v(BufferBuilder builder,org.joml.Matrix4f matrix,int x,int y,double dx,double dy){builder.addVertex(matrix,(float)(x+dx),(float)(y+dy),0).setUv((float)((176+dx)/256D),(float)((64+dy)/256D));}
    private GasTurbineGauge(){}
}
