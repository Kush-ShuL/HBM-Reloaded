package com.hbm.client.forcefield;
import com.mojang.blaze3d.vertex.*;import net.minecraft.client.renderer.RenderType;
/** Actual untextured opaque1px GL_LINES, unlit/no-cull with ordinary depth test/write. */
final class ForceFieldRenderTypes extends RenderType {
 private ForceFieldRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.DEBUG_LINES,256,false,false,()->{},()->{});}
 static final RenderType WIREFRAME=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_forcefield_wireframe",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.DEBUG_LINES,262144,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setCullState(NO_CULL).createCompositeState(false)),"ForceField/Wireframe");
}
