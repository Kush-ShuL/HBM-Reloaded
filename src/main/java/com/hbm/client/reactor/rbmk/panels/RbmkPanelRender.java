package com.hbm.client.reactor.rbmk.panels;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.model.*;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
/** Exact source OBJ primitives and actual current Minecraft.font; a procedural white texel implements old GL_TEXTURE_2D=false. */
public final class RbmkPanelRender{
    private static ResourceLocation white;
    private static ResourceLocation white(){if(white==null){var pixels=new NativeImage(1,1,false);pixels.setPixelRGBA(0,0,-1);white=ResourceLocation.parse("hbm:rbmk_panel_white");Minecraft.getInstance().getTextureManager().register(white,new DynamicTexture(pixels));}return white;}
    public static void mesh(PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation model,ResourceLocation texture,String part,int color,boolean untextured){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);var vertices=buffers.getBuffer(RenderType.entityCutout(untextured?white():texture));if(part==null)LegacyObjRenderer.renderAll(mesh,poses,vertices,light,overlay,color,LegacyObjRenderer.Convention.HFR);else LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,color,LegacyObjRenderer.Convention.HFR,part);}
    public static Font font(){return LegacyFonts.get();}public static void text(String text,int x,int y,int color,PoseStack poses,MultiBufferSource buffers,int light){font().drawInBatch(text,x,y,0xff000000|color,false,poses.last().pose(),buffers,Font.DisplayMode.NORMAL,0,light);}
    public static String shortNumber(long value){double result;String suffix;long absolute=Math.abs(value);if(absolute>=Math.pow(10,18)){result=value/Math.pow(10,18);suffix="E";}else if(absolute>=Math.pow(10,15)){result=value/Math.pow(10,15);suffix="P";}else if(absolute>=Math.pow(10,12)){result=value/Math.pow(10,12);suffix="T";}else if(absolute>=Math.pow(10,9)){result=value/Math.pow(10,9);suffix="G";}else if(absolute>=Math.pow(10,6)){result=value/Math.pow(10,6);suffix="M";}else if(absolute>=Math.pow(10,3)){result=value/Math.pow(10,3);suffix="k";}else return Long.toString(value);result=result<=-100?Math.round(result*10)/10D:Math.round(result*100)/100D;return result+suffix;}
    private RbmkPanelRender(){}
}
