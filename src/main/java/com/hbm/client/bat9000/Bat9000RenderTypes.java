package com.hbm.client.bat9000;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** RenderBAT9000 level windows are untextured,unlit,opaque,color+depth writes,no culling. */
final class Bat9000RenderTypes extends RenderType {
 private Bat9000RenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 static final RenderType WINDOW=create("hbm_bat9000_window",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
}
