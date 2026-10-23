package com.hbm.client.model;

import com.hbm.fluid.HbmFluidType;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Exact original DiamondPronter atlas rectangles, floating planes, digit/symbol sizes. */
public final class LegacyDangerDiamond extends RenderType {
    private LegacyDangerDiamond(){super("unused",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final RenderType TYPE=create("hbm_danger_diamond",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/misc/danger_diamond.png"),false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false));
    public static void render(PoseStack poses,MultiBufferSource buffers,int light,HbmFluidType type){
        var out=buffers.getBuffer(TYPE);quad(poses,out,light,0,.5F,-.5F,-.5F,.5F,144F/256,5F/256,45F/256,184F/256);
        digit(poses,out,light,type.poison,0,33F/139);digit(poses,out,light,type.flammability,33F/139,0);digit(poses,out,light,type.reactivity,0,-33F/139);
        if(type.symbol!=HbmFluidType.Symbol.NONE){float size=59F/2/139,y=-33F/139;int x=type.symbol.x,v=type.symbol.y;quad(poses,out,light,.01F,size+y,-size+y,-size,size,(x+59F)/256,x/256F,v/256F,(v+59F)/256);}
    }
    private static void digit(PoseStack poses,VertexConsumer out,int light,int number,float y,float z){if(number<0||number>=6)return;int x=number==0?125:5+(number-1)*24;float width=10F/139,height=14F/139;quad(poses,out,light,.01F,height+y,-height+y,-width+z,width+z,(x+20F)/256,x/256F,5F/256,33F/256);}
    private static void quad(PoseStack poses,VertexConsumer out,int light,float x,float top,float bottom,float left,float right,float umax,float umin,float vmin,float vmax){var matrix=poses.last().pose();out.addVertex(matrix,x,top,left).setUv(umax,vmin).setColor(255,255,255,255).setLight(light);out.addVertex(matrix,x,top,right).setUv(umin,vmin).setColor(255,255,255,255).setLight(light);out.addVertex(matrix,x,bottom,right).setUv(umin,vmax).setColor(255,255,255,255).setLight(light);out.addVertex(matrix,x,bottom,left).setUv(umax,vmax).setColor(255,255,255,255).setLight(light);}
}
