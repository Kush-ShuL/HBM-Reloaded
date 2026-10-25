package com.hbm.client.weapon.chemthrower;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.hbm.client.weapon.forward.GunForwardEffects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
final class ChemicalRenderTypes extends RenderType {
 private static final TransparencyStateShard ADDITIVE=new TransparencyStateShard("hbm_chemical_beam_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
 private static final WriteMaskStateShard ANTIMATTER_DEPTH=new WriteMaskStateShard(true,true){
  private boolean previousDepthWrite;
  @Override public void setupRenderState(){
   previousDepthWrite=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
   // COLOR_DEPTH_WRITE itself does not enable writes when Iris's forward base is false.
   RenderSystem.depthMask(true);GL11.glDepthMask(true);
  }
  @Override public void clearRenderState(){RenderSystem.depthMask(previousDepthWrite);GL11.glDepthMask(previousDepthWrite);}
 };
 static final RenderType BEAM=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_chemical_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,2048,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"Chemthrower/WorldBeam");
 static final RenderType ANTIMATTER_BEAM=GunForwardEffects.worldOccluder(GunForwardEffects.zeroAlphaDiscard(GunForwardEffects.additive(create("hbm_chemical_antimatter_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,2048,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE).setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(NO_CULL).setWriteMaskState(ANTIMATTER_DEPTH).createCompositeState(false)),ResourceLocation.withDefaultNamespace("textures/misc/white.png"),true,false,false)));
 private ChemicalRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
