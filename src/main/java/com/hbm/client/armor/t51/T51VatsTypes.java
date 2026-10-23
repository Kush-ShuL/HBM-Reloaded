package com.hbm.client.armor.t51;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
/** Exact non-thermal FSB label background: alpha blend, depth test, no depth write. */
final class T51VatsTypes extends RenderType {
 static final RenderType BACKGROUND=create("hbm_t51_vats_background",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
 static final RenderType THERMAL_BACKGROUND=create("hbm_fsb_thermal_vats_background",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,true,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(NO_DEPTH_TEST).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
 static RenderType background(boolean thermal){return thermal?THERMAL_BACKGROUND:BACKGROUND;}
 private T51VatsTypes(String name,VertexFormat format,VertexFormat.Mode mode,int size,boolean crumbling,boolean sort,Runnable setup,Runnable clear){super(name,format,mode,size,crumbling,sort,setup,clear);}
}
