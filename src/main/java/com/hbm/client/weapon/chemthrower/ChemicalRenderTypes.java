package com.hbm.client.weapon.chemthrower;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
final class ChemicalRenderTypes extends RenderType {
 private static final TransparencyStateShard ADDITIVE=new TransparencyStateShard("hbm_chemical_beam_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
 static final RenderType BEAM=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_chemical_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,2048,false,false,CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(ADDITIVE).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"Chemthrower/WorldBeam");
 private ChemicalRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
