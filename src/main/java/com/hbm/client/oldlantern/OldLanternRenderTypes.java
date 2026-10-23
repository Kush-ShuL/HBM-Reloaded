package com.hbm.client.oldlantern;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Original untextured opaque/unlit no-cull lamp, ordinary depth test and depth writes. */
final class OldLanternRenderTypes extends RenderType {
    private OldLanternRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType LIGHT=create("hbm_old_lantern_light",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    static VertexConsumer colorOnly(VertexConsumer out){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){return this;}public VertexConsumer setUv1(int u,int v){return this;}public VertexConsumer setUv2(int u,int v){return this;}public VertexConsumer setNormal(float x,float y,float z){return this;}};}
}
